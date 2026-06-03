package com.poorbet.authservice.user.service;

import com.poorbet.authservice.user.dto.JwtResponse;
import com.poorbet.authservice.user.dto.UserLoginDto;
import com.poorbet.authservice.user.dto.UserRegisterDto;
import com.poorbet.authservice.user.dto.UserResponseDto;

public interface UserService {

    UserResponseDto register(UserRegisterDto registerDto);

    boolean emailExists(String email);

    JwtResponse login(UserLoginDto loginDto);
}
