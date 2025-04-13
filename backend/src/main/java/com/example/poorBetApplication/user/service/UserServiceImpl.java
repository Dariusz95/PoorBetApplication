package com.example.poorBetApplication.user.service;

import com.example.poorBetApplication.exception.UserAlreadyExistsException;
import com.example.poorBetApplication.user.dto.UserRegisterDto;
import com.example.poorBetApplication.user.model.Role;
import com.example.poorBetApplication.user.model.User;
import com.example.poorBetApplication.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService  {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(UserRegisterDto dto){
        if(userRepository.existsByEmail(dto.email())){
            throw new UserAlreadyExistsException("Email is already in use: " + dto.email());
        }

        User user = new User();

        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}
