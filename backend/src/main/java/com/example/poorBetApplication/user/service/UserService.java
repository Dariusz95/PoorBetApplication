package com.example.poorBetApplication.user.service;

import com.example.poorBetApplication.user.dto.UserRegisterDto;
import com.example.poorBetApplication.user.dto.UserResponseDto;

public interface UserService {

    UserResponseDto register(UserRegisterDto registerDto);

    boolean emailExists(String email);

}
