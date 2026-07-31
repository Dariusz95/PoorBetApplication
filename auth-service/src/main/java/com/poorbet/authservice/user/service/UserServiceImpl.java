package com.poorbet.authservice.user.service;

import com.poorbet.authservice.config.TestUserProperties;
import com.poorbet.authservice.exception.InvalidCredentialsException;
import com.poorbet.authservice.exception.InvalidRefreshTokenException;
import com.poorbet.authservice.exception.ResourceAlreadyExistsException;
import com.poorbet.authservice.security.JwtUtil;
import com.poorbet.authservice.user.dto.*;
import com.poorbet.authservice.user.mapper.UserMapper;
import com.poorbet.authservice.user.model.User;
import com.poorbet.authservice.user.repository.UserRepository;
import com.poorbet.authstarter.security.PoorbetTokenTypes;
import com.poorbet.commons.commons.auth.UserBatchLookupResponse;
import com.poorbet.commons.commons.auth.UserDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthorizationPolicyService authorizationPolicyService;
    private final UserCreatedEventPublisher publisher;
    private final TestUserProperties testUserProperties;

    @Override
    @Transactional
    public UserResponseDto register(UserRegisterDto registerDto) {
        logger.debug("New user registration: {}", registerDto.email());

        if (emailExists(registerDto.email())) {
            logger.warn("Attempting to register with an existing email address: {}", registerDto.email());
            throw new ResourceAlreadyExistsException("User with email address " + registerDto.email() + " already exists");
        }

        User user = userMapper.toEntity(registerDto);
        User savedUser = userRepository.save(user);

        logger.info("User successfully registered: {}", savedUser.getEmail());

        publisher.publishUserCreated(savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public JwtResponse login(UserLoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.email())
                .filter(User::isActive)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password."));

        if (!passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        return buildJwtResponse(user);
    }

    @Override
    public JwtResponse loginAsTestUser() {
        User user = userRepository.findByEmail(testUserProperties.getEmail())
                .filter(User::isActive)
                .orElseThrow(() -> new InvalidCredentialsException("The test account is currently unavailable."));

        return buildJwtResponse(user);
    }

    @Override
    public JwtResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException("Invalid or expired refresh token.");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .filter(User::isActive)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid or expired refresh token."));

        return buildJwtResponse(user);
    }

    @Override
    public void logout(RefreshTokenRequest request) {
        if (request == null || request.refreshToken() == null) {
            return;
        }

        try {
            String email = jwtUtil.getEmailFromToken(request.refreshToken());
            logger.info("User logged out: {}", email);
        } catch (Exception e) {
            logger.debug("Logout with an unreadable refresh token — skipping email logging.");
        }
    }

    private JwtResponse buildJwtResponse(User user) {
        List<String> roles = List.of(user.getRole().name());
        List<String> permissions = authorizationPolicyService.resolvePermissions(user.getRole());
        String token = jwtUtil.generateAccessToken(
                user.getEmail(),
                roles,
                permissions,
                PoorbetTokenTypes.USER,
                user.getId().toString(),
                null,
                List.of()
        );
        long expiresAt = System.currentTimeMillis() + jwtUtil.getAccessTokenExpiration();

        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        long refreshExpiresAt = System.currentTimeMillis() + jwtUtil.getRefreshTokenExpiration();

        return new JwtResponse(token, user.getEmail(), roles, permissions, expiresAt, refreshToken, refreshExpiresAt);
    }

    @Override
    public UserBatchLookupResponse lookup(Set<UUID> ids) {
        Map<UUID, UserDto> userMap = userRepository.findByIdIn(ids)
                .stream()
                .map(user -> new UserDto(user.getId(), user.getEmail()))
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));

        return new UserBatchLookupResponse(userMap);
    }
}
