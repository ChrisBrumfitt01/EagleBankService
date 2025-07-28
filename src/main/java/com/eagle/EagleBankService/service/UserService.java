package com.eagle.EagleBankService.service;

import com.eagle.EagleBankService.dto.UpdateUserRequest;
import com.eagle.EagleBankService.dto.UserRequest;
import com.eagle.EagleBankService.dto.UserResponse;
import com.eagle.EagleBankService.entity.UserEntity;
import com.eagle.EagleBankService.exception.ConflictException;
import com.eagle.EagleBankService.exception.ForbiddenException;
import com.eagle.EagleBankService.exception.NotFoundException;
import com.eagle.EagleBankService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
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
    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request, String authenticatedEmail){
        UserEntity user = findUserAndValidate(userId, authenticatedEmail);
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
    @Transactional
    public void deleteUser(UUID userId, String authenticatedEmail){
        UserEntity user = findUserAndValidate(userId, authenticatedEmail);
        if(!user.getAccounts().isEmpty()){
            throw new ConflictException("Cannot delete a user that has accounts");
        }

        userRepository.delete(user);
    }
    @Transactional(readOnly = true)
    public Optional<UserEntity> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    private UserEntity findUserAndValidate(UUID userId, String authenticatedEmail) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Could not find user with ID {}. Throwing NotFoundException.", userId);
                    return new NotFoundException(String.format("User not found with the ID: %s", userId));
                });
        if (!user.getEmail().equals(authenticatedEmail)) {
            log.warn("User email of {} does not match authenticated user email of {}. Throwing ForbiddenException.", user.getEmail(), authenticatedEmail);
            throw new ForbiddenException("Operation forbidden: Different user");
        }
        return user;
    }
}
