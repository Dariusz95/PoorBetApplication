package com.example.poorBetApplication.user.controller;

import com.example.poorBetApplication.user.dto.UserRegisterDto;
import com.example.poorBetApplication.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Console;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Validated @RequestBody UserRegisterDto dto){

        System.out.print("Created");
        userService.register(dto
        );
    }
}
