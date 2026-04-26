package com.vikram.ems.service;

import com.vikram.ems.dto.request.LoginRequest;
import com.vikram.ems.dto.request.RegisterRequest;
import com.vikram.ems.dto.response.JwtResponse;
import com.vikram.ems.entity.Role;
import com.vikram.ems.entity.User;
import com.vikram.ems.exception.DuplicateResourceException;
import com.vikram.ems.repository.RoleRepository;
import com.vikram.ems.repository.UserRepository;
import com.vikram.ems.security.JwtTokenProvider;
import com.vikram.ems.security.UserDetailsImpl;
import com.vikram.ems.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider tokenProvider;

    @InjectMocks private AuthServiceImpl authService;

    private UserDetailsImpl userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        userDetails = new UserDetailsImpl(
                1L, "admin", "admin@ems.com", "encoded_password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    // -------- login --------

    @Test
    @DisplayName("login - should return JWT response on valid credentials")
    void login_success() {
        LoginRequest request = new LoginRequest("admin", "admin123");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("mock.jwt.token");

        JwtResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getRoles()).contains("ROLE_ADMIN");
        assertThat(response.getType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("login - should throw BadCredentialsException on wrong password")
    void login_badCredentials() {
        LoginRequest request = new LoginRequest("admin", "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    // -------- register --------

    @Test
    @DisplayName("register - should register user with default VIEWER role")
    void register_success_defaultRole() {
        RegisterRequest request = new RegisterRequest(
                "newuser", "password123", "new@ems.com", null);

        Role viewerRole = new Role(3, "ROLE_VIEWER");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@ems.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_VIEWER")).thenReturn(Optional.of(viewerRole));
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        String result = authService.register(request);

        assertThat(result).contains("successfully");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register - should throw when username already exists")
    void register_duplicateUsername() {
        RegisterRequest request = new RegisterRequest(
                "admin", "password123", "other@ems.com", null);

        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("admin");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register - should throw when email already exists")
    void register_duplicateEmail() {
        RegisterRequest request = new RegisterRequest(
                "newuser", "password123", "admin@ems.com", null);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("admin@ems.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("admin@ems.com");
    }

    @Test
    @DisplayName("register - should assign specified roles")
    void register_withSpecifiedRole() {
        RegisterRequest request = new RegisterRequest(
                "hruser2", "password123", "hr2@ems.com", Set.of("ROLE_HR"));

        Role hrRole = new Role(2, "ROLE_HR");

        when(userRepository.existsByUsername("hruser2")).thenReturn(false);
        when(userRepository.existsByEmail("hr2@ems.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_HR")).thenReturn(Optional.of(hrRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        String result = authService.register(request);

        assertThat(result).contains("successfully");
        verify(roleRepository).findByName("ROLE_HR");
    }
}