package com.poorbet.authservice.user.service;

import com.poorbet.authservice.user.dto.*;
import com.poorbet.commons.commons.auth.UserBatchLookupResponse;

import java.util.Set;
import java.util.UUID;

public interface UserService {

    UserResponseDto register(UserRegisterDto registerDto);

    boolean emailExists(String email);

    JwtResponse login(UserLoginDto loginDto);

    UserBatchLookupResponse lookup(Set<UUID> ids);
}
