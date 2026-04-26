package com.vikram.ems.service;

import com.vikram.ems.dto.request.LoginRequest;
import com.vikram.ems.dto.request.RegisterRequest;
import com.vikram.ems.dto.response.JwtResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    String register(RegisterRequest request);
}