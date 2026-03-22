package com.poorbet.users.user.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poorbet.commons.security.PoorbetTokenTypes;
import com.poorbet.users.exception.ResourceAlreadyExistsException;
import com.poorbet.users.security.JwtUtil;
import com.poorbet.users.user.dto.JwtResponse;
import com.poorbet.users.user.dto.UserLoginDto;
import com.poorbet.users.user.dto.UserRegisterDto;
import com.poorbet.users.user.dto.UserResponseDto;
import com.poorbet.users.user.mapper.UserMapper;
import com.poorbet.users.user.model.User;
import com.poorbet.users.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService  {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthorizationPolicyService authorizationPolicyService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                           AuthorizationPolicyService authorizationPolicyService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authorizationPolicyService = authorizationPolicyService;
    }

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
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        List<String> roles = List.of(user.getRole().name());
        List<String> permissions = authorizationPolicyService.resolvePermissions(user.getRole());
        String token = jwtUtil.generateAccessToken(
                user.getEmail(),
                roles,
                permissions,
                PoorbetTokenTypes.USER,
                null,
                List.of()
        );
        long expiresAt = System.currentTimeMillis() + jwtUtil.getAccessTokenExpiration();

        return new JwtResponse(token, user.getEmail(), roles, permissions, expiresAt);
    }
}
