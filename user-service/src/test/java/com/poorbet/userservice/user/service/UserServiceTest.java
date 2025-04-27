package com.poorbet.userservice.user.service;


import com.poorbet.userservice.exception.ResourceAlreadyExistsException;
import com.poorbet.userservice.user.dto.UserRegisterDto;
import com.poorbet.userservice.user.dto.UserResponseDto;
import com.poorbet.userservice.user.model.Role;
import com.poorbet.userservice.user.model.User;
import com.poorbet.userservice.user.repository.UserRepository;
import com.poorbet.userservice.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

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

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setRole(Role.USER);
        mockUser.setActive(true);

        mockResponseDto = new UserResponseDto(
                1L,
                "test@example.com",
                "USER",
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
}