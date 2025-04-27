package com.poorbet.userservice.user.service;

import com.poorbet.userservice.user.dto.UserRegisterDto;
import com.poorbet.userservice.user.dto.UserResponseDto;

public interface UserService {

    UserResponseDto register(UserRegisterDto registerDto);

    boolean emailExists(String email);

}
