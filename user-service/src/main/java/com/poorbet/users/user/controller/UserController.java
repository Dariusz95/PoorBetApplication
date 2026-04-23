package com.poorbet.users.user.controller;

import com.poorbet.users.user.dto.JwtResponse;
import com.poorbet.users.user.dto.UserLoginDto;
import com.poorbet.users.user.dto.UserRegisterDto;
import com.poorbet.users.user.dto.UserResponseDto;
import com.poorbet.users.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegisterDto registerDto) {
        logger.debug("A request for registration has been received for: {}", registerDto.email());
        UserResponseDto createdUser = userService.register(registerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/email-exists")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody UserLoginDto loginDto) {
        JwtResponse jwtResponse = userService.login(loginDto);
        return ResponseEntity.ok(jwtResponse);
    }
}
