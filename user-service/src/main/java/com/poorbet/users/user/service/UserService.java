package com.poorbet.users.user.service;

import com.poorbet.users.user.dto.JwtResponse;
import com.poorbet.users.user.dto.UserLoginDto;
import com.poorbet.users.user.dto.UserRegisterDto;
import com.poorbet.users.user.dto.UserResponseDto;

public interface UserService {

    UserResponseDto register(UserRegisterDto registerDto);

    boolean emailExists(String email);

    JwtResponse login(UserLoginDto loginDto);
}
