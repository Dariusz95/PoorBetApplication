package com.poorbet.authservice.user.service;


import com.poorbet.authservice.exception.ResourceAlreadyExistsException;
import com.poorbet.authservice.security.JwtUtil;
import com.poorbet.authservice.user.dto.UserRegisterDto;
import com.poorbet.authservice.user.dto.UserResponseDto;
import com.poorbet.authservice.user.mapper.UserMapper;
import com.poorbet.authservice.user.model.Role;
import com.poorbet.authservice.user.model.User;
import com.poorbet.authservice.user.repository.UserRepository;
import com.poorbet.commons.commons.auth.UserBatchLookupResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

        // register() rejestruje TransactionSynchronization wewnątrz @Transactional; bez kontekstu
        // Springa nikt tego nie inicjuje, więc trzeba to zasymulować ręcznie na potrzeby testu.
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDown() {
        TransactionSynchronizationManager.clearSynchronization();
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
}