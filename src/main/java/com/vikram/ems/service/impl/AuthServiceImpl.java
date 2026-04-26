package com.vikram.ems.service.impl;

import com.vikram.ems.dto.request.LoginRequest;
import com.vikram.ems.dto.request.RegisterRequest;
import com.vikram.ems.dto.response.JwtResponse;
import com.vikram.ems.entity.Role;
import com.vikram.ems.entity.User;
import com.vikram.ems.exception.DuplicateResourceException;
import com.vikram.ems.exception.ResourceNotFoundException;
import com.vikram.ems.repository.RoleRepository;
import com.vikram.ems.repository.UserRepository;
import com.vikram.ems.security.JwtTokenProvider;
import com.vikram.ems.security.UserDetailsImpl;
import com.vikram.ems.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        log.info("User '{}' logged in successfully", userDetails.getUsername());

        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email '" + request.getEmail() + "' is already in use");
        }

        Set<Role> roles = new HashSet<>();

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            Role viewerRole = roleRepository.findByName("ROLE_VIEWER")
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_VIEWER"));
            roles.add(viewerRole);
        } else {
            request.getRoles().forEach(roleName -> {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
                roles.add(role);
            });
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(roles)
                .build();

        userRepository.save(user);
        log.info("New user registered: '{}'", request.getUsername());

        return "User registered successfully";
    }
}