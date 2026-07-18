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
@Profile("dev")
@RequiredArgsConstructor
public class DevTestUserSeeder implements ApplicationRunner {

    private static final String TEST_USER_EMAIL = "test@test.pl";
    private static final String TEST_USER_PASSWORD = "zaq1@WSX";

    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) {
        if (userService.emailExists(TEST_USER_EMAIL)) {
            return;
        }

        userService.register(new UserRegisterDto(TEST_USER_EMAIL, TEST_USER_PASSWORD));
        log.info("A test account has been created for the dev profile: {}", TEST_USER_EMAIL);
    }
}
