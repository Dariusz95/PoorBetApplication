package com.poorbet.authservice.user.service;

import com.poorbet.authservice.exception.InvalidCredentialsException;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        logger.info("UserCreatedEvent sent to Rabbit. userId={}", savedUser.getId());

                        publisher.publishUserCreated(savedUser.getId());
                    }
                }
        );

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
                .orElseThrow(() -> new InvalidCredentialsException("Nieprawidłowy adres e-mail lub hasło."));

        if (!passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Nieprawidłowy adres e-mail lub hasło.");
        }

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

        return new JwtResponse(token, user.getEmail(), roles, permissions, expiresAt);
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
