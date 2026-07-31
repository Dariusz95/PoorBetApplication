package com.poorbet.authservice.config;

import com.poorbet.authservice.user.dto.UserRegisterDto;
import com.poorbet.authservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class TestUserSeeder implements ApplicationRunner {

    private final UserService userService;
    private final TestUserProperties testUserProperties;

    @Override
    public void run(ApplicationArguments args) {
        String email = testUserProperties.getEmail();

        if (userService.emailExists(email)) {
            return;
        }

        userService.register(new UserRegisterDto(email, testUserProperties.getPassword()));
        log.info("A test account has been created: {}", email);
    }
}
