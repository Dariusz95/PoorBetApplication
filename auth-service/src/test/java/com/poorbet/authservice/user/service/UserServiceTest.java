package com.poorbet.authservice.user.service;


import com.poorbet.authservice.config.TestUserProperties;
import com.poorbet.authservice.exception.InvalidCredentialsException;
import com.poorbet.authservice.exception.InvalidRefreshTokenException;
import com.poorbet.authservice.exception.ResourceAlreadyExistsException;
import com.poorbet.authservice.security.JwtUtil;
import com.poorbet.authservice.user.dto.JwtResponse;
import com.poorbet.authservice.user.dto.RefreshTokenRequest;
import com.poorbet.authservice.user.dto.UserLoginDto;
import com.poorbet.authservice.user.dto.UserRegisterDto;
import com.poorbet.authservice.user.dto.UserResponseDto;
import com.poorbet.authservice.user.mapper.UserMapper;
import com.poorbet.authservice.user.model.Role;
import com.poorbet.authservice.user.model.User;
import com.poorbet.authservice.user.repository.UserRepository;
import com.poorbet.commons.commons.auth.UserBatchLookupResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthorizationPolicyService authorizationPolicyService;

    @Mock
    private TestUserProperties testUserProperties;

    @Mock
    private UserCreatedEventPublisher publisher;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegisterDto validUserDto;
    private User mockUser;
    private UserResponseDto mockResponseDto;

    @BeforeEach
    void setUp() {
        validUserDto = new UserRegisterDto(
                "test@example.com",
                "Password123"
        );

        UUID uuid = UUID.randomUUID();
        String email = "test@example.com";

        mockUser = new User();
        mockUser.setId(uuid);
        mockUser.setEmail(email);
        mockUser.setRole(Role.USER);
        mockUser.setActive(true);

        mockResponseDto = new UserResponseDto(
                uuid,
                email,
                Role.USER,
                mockUser.getCreatedAt()
        );
    }

    @Test
    void register_WithValidData_ShouldReturnUserResponse() {
        // Given
        when(userRepository.existsByEmail(validUserDto.email())).thenReturn(false);
        when(userMapper.toEntity(validUserDto)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(mockUser)).thenReturn(mockResponseDto);

        // When
        UserResponseDto result = userService.register(validUserDto);

        // Then
        assertNotNull(result);
        assertEquals(mockResponseDto.email(), result.email());
        assertEquals(mockResponseDto.role(), result.role());

        verify(userRepository).existsByEmail(validUserDto.email());
        verify(userMapper).toEntity(validUserDto);
        verify(userRepository).save(mockUser);
        verify(userMapper).toDto(mockUser);
        verify(publisher).publishUserCreated(mockUser.getId());
    }

    @Test
    void register_WithExistingEmail_ShouldThrowException() {
        // Given
        when(userRepository.existsByEmail(validUserDto.email())).thenReturn(true);

        // When & Then
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            userService.register(validUserDto);
        });

        verify(userRepository).existsByEmail(validUserDto.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void emailExists_WithExistingEmail_ShouldReturnTrue() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When
        boolean result = userService.emailExists("test@example.com");

        // Then
        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void emailExists_WithNonExistingEmail_ShouldReturnFalse() {
        // Given
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // When
        boolean result = userService.emailExists("nonexistent@example.com");

        // Then
        assertFalse(result);
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }

    @Test
    void lookup_WithExistingIds_ShouldReturnMapKeyedByUserId() {
        // Given
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();

        User firstUser = new User();
        firstUser.setId(firstId);
        firstUser.setEmail("first@example.com");

        User secondUser = new User();
        secondUser.setId(secondId);
        secondUser.setEmail("second@example.com");

        Set<UUID> requestedIds = Set.of(firstId, secondId);
        when(userRepository.findByIdIn(requestedIds)).thenReturn(List.of(firstUser, secondUser));

        // When
        UserBatchLookupResponse result = userService.lookup(requestedIds);

        // Then
        assertEquals(2, result.users().size());
        assertEquals("first@example.com", result.users().get(firstId).getEmail());
        assertEquals("second@example.com", result.users().get(secondId).getEmail());
    }

    @Test
    void lookup_WithNoMatchingUsers_ShouldReturnEmptyMap() {
        // Given
        Set<UUID> requestedIds = Set.of(UUID.randomUUID());
        when(userRepository.findByIdIn(requestedIds)).thenReturn(List.of());

        // When
        UserBatchLookupResponse result = userService.lookup(requestedIds);

        // Then
        assertTrue(result.users().isEmpty());
        verify(userRepository).findByIdIn(requestedIds);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnJwtResponseWithAccessAndRefreshTokens() {
        // Given
        UserLoginDto loginDto = new UserLoginDto("test@example.com", "Password123");
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(mockUser));
        when(passwordEncoder.matches("Password123", mockUser.getPassword())).thenReturn(true);
        when(authorizationPolicyService.resolvePermissions(Role.USER)).thenReturn(List.of("MATCH_READ"));
        when(jwtUtil.generateAccessToken(any(), any(), any(), any(), any(), any(), any())).thenReturn("access-token");
        when(jwtUtil.getAccessTokenExpiration()).thenReturn(900000L);
        when(jwtUtil.generateRefreshToken("test@example.com")).thenReturn("refresh-token");
        when(jwtUtil.getRefreshTokenExpiration()).thenReturn(604800000L);

        // When
        JwtResponse result = userService.login(loginDto);

        // Then
        assertEquals("access-token", result.getToken());
        assertEquals("refresh-token", result.getRefreshToken());
        assertTrue(result.getRefreshExpiresAt() > 0);
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowInvalidCredentialsException() {
        // Given
        UserLoginDto loginDto = new UserLoginDto("test@example.com", "WrongPass123");
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(mockUser));
        when(passwordEncoder.matches("WrongPass123", mockUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> userService.login(loginDto));
    }

    @Test
    void loginAsTestUser_WithSeededTestUser_ShouldReturnJwtResponse() {
        // Given
        when(testUserProperties.getEmail()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(mockUser));
        when(authorizationPolicyService.resolvePermissions(Role.USER)).thenReturn(List.of("MATCH_READ"));
        when(jwtUtil.generateAccessToken(any(), any(), any(), any(), any(), any(), any())).thenReturn("access-token");
        when(jwtUtil.getAccessTokenExpiration()).thenReturn(900000L);
        when(jwtUtil.generateRefreshToken("test@example.com")).thenReturn("refresh-token");
        when(jwtUtil.getRefreshTokenExpiration()).thenReturn(604800000L);

        // When
        JwtResponse result = userService.loginAsTestUser();

        // Then
        assertEquals("access-token", result.getToken());
        assertEquals("test@example.com", result.getUsername());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void loginAsTestUser_WithMissingTestUser_ShouldThrowInvalidCredentialsException() {
        // Given
        when(testUserProperties.getEmail()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> userService.loginAsTestUser());
    }

    @Test
    void refresh_WithValidRefreshToken_ShouldReturnNewJwtResponse() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        when(jwtUtil.validateToken("refresh-token")).thenReturn(true);
        when(jwtUtil.isRefreshToken("refresh-token")).thenReturn(true);
        when(jwtUtil.getEmailFromToken("refresh-token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(mockUser));
        when(authorizationPolicyService.resolvePermissions(Role.USER)).thenReturn(List.of("MATCH_READ"));
        when(jwtUtil.generateAccessToken(any(), any(), any(), any(), any(), any(), any())).thenReturn("new-access-token");
        when(jwtUtil.getAccessTokenExpiration()).thenReturn(900000L);
        when(jwtUtil.generateRefreshToken("test@example.com")).thenReturn("new-refresh-token");
        when(jwtUtil.getRefreshTokenExpiration()).thenReturn(604800000L);

        // When
        JwtResponse result = userService.refresh(request);

        // Then
        assertEquals("new-access-token", result.getToken());
        assertEquals("new-refresh-token", result.getRefreshToken());
    }

    @Test
    void refresh_WithInvalidSignature_ShouldThrowInvalidRefreshTokenException() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("garbage");
        when(jwtUtil.validateToken("garbage")).thenReturn(false);

        // When & Then
        assertThrows(InvalidRefreshTokenException.class, () -> userService.refresh(request));
    }

    @Test
    void refresh_WithAccessTokenPresentedAsRefreshToken_ShouldThrowInvalidRefreshTokenException() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("access-token");
        when(jwtUtil.validateToken("access-token")).thenReturn(true);
        when(jwtUtil.isRefreshToken("access-token")).thenReturn(false);

        // When & Then
        assertThrows(InvalidRefreshTokenException.class, () -> userService.refresh(request));
    }

    @Test
    void refresh_WithInactiveUser_ShouldThrowInvalidRefreshTokenException() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        when(jwtUtil.validateToken("refresh-token")).thenReturn(true);
        when(jwtUtil.isRefreshToken("refresh-token")).thenReturn(true);
        when(jwtUtil.getEmailFromToken("refresh-token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(InvalidRefreshTokenException.class, () -> userService.refresh(request));
    }

    @Test
    void logout_WithNullRequest_ShouldNotThrow() {
        assertDoesNotThrow(() -> userService.logout(null));
    }

    @Test
    void logout_WithValidRefreshToken_ShouldNotThrow() {
        // Given
        when(jwtUtil.getEmailFromToken("refresh-token")).thenReturn("test@example.com");

        // When & Then
        assertDoesNotThrow(() -> userService.logout(new RefreshTokenRequest("refresh-token")));
    }

    @Test
    void logout_WithUnparsableRefreshToken_ShouldNotThrow() {
        // Given
        when(jwtUtil.getEmailFromToken("garbage")).thenThrow(new RuntimeException("malformed token"));

        // When & Then
        assertDoesNotThrow(() -> userService.logout(new RefreshTokenRequest("garbage")));
    }
}