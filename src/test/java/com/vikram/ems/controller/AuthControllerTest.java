package com.vikram.ems.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vikram.ems.dto.request.LoginRequest;
import com.vikram.ems.dto.response.JwtResponse;
import com.vikram.ems.exception.GlobalExceptionHandler;
import com.vikram.ems.security.JwtAuthEntryPoint;
import com.vikram.ems.security.JwtAuthenticationFilter;
import com.vikram.ems.security.JwtTokenProvider;
import com.vikram.ems.security.UserDetailsServiceImpl;
import com.vikram.ems.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthService authService;
    @MockBean private JwtTokenProvider tokenProvider;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Test
    @DisplayName("POST /api/auth/login - 200 OK with valid credentials")
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest("admin", "admin123");

        JwtResponse jwtResponse = JwtResponse.builder()
                .token("mock.jwt.token")
                .type("Bearer")
                .id(1L)
                .username("admin")
                .email("admin@ems.com")
                .roles(List.of("ROLE_ADMIN"))
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("POST /api/auth/login - 401 for bad credentials")
    void login_badCredentials() throws Exception {
        LoginRequest request = new LoginRequest("admin", "wrongpassword");

        when(authService.login(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - 400 when username is blank")
    void login_validation_blankUsername() throws Exception {
        LoginRequest request = new LoginRequest("", "admin123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - 400 when password is blank")
    void login_validation_blankPassword() throws Exception {
        LoginRequest request = new LoginRequest("admin", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}