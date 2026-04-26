package com.vikram.ems.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    // Valid 256-bit Base64 encoded secret for testing
    private static final String TEST_SECRET =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION_MS = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", EXPIRATION_MS);
    }

    private Authentication buildAuthentication(String username) {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, username, username + "@ems.com", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    @Test
    @DisplayName("generateToken - should return non-null JWT string")
    void generateToken_returnsToken() {
        Authentication auth = buildAuthentication("admin");
        String token = tokenProvider.generateToken(auth);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    @DisplayName("getUsernameFromToken - should extract correct username")
    void getUsernameFromToken_correctUsername() {
        Authentication auth = buildAuthentication("vikram");
        String token = tokenProvider.generateToken(auth);

        String username = tokenProvider.getUsernameFromToken(token);

        assertThat(username).isEqualTo("vikram");
    }

    @Test
    @DisplayName("validateToken - should return true for valid token")
    void validateToken_validToken() {
        Authentication auth = buildAuthentication("admin");
        String token = tokenProvider.generateToken(auth);

        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("validateToken - should return false for malformed token")
    void validateToken_malformedToken() {
        assertThat(tokenProvider.validateToken("this.is.notvalid")).isFalse();
    }

    @Test
    @DisplayName("validateToken - should return false for empty string")
    void validateToken_emptyToken() {
        assertThat(tokenProvider.validateToken("")).isFalse();
    }

    @Test
    @DisplayName("validateToken - should return false for expired token")
    void validateToken_expiredToken() {
        // Set expiration to -1ms (already expired)
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", -1000L);
        Authentication auth = buildAuthentication("admin");
        String expiredToken = tokenProvider.generateToken(auth);

        assertThat(tokenProvider.validateToken(expiredToken)).isFalse();
    }
}