package com.eagle.EagleBankService.service;

import com.eagle.EagleBankService.dto.UpdateUserRequest;
import com.eagle.EagleBankService.dto.UserRequest;
import com.eagle.EagleBankService.dto.UserResponse;
import com.eagle.EagleBankService.entity.UserEntity;
import com.eagle.EagleBankService.exception.ForbiddenException;
import com.eagle.EagleBankService.exception.NotFoundException;
import com.eagle.EagleBankService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserResponse createUser(UserRequest request) {
        UserEntity user = UserEntity.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }

    public UserResponse updateUser(UUID userId, UpdateUserRequest request, String authenticatedEmail){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found with the ID: %s", userId)));

        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new ForbiddenException("You are forbidden to update this user");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        UserEntity updated = userRepository.save(user);

        return UserResponse.builder()
                .id(updated.getId())
                .fullName(updated.getFullName())
                .email(updated.getEmail())
                .build();
    }

    public Optional<UserEntity> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
