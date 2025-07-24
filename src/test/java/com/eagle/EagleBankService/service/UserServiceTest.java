package com.eagle.EagleBankService.service;

import com.eagle.EagleBankService.dto.UpdateUserRequest;
import com.eagle.EagleBankService.dto.UserRequest;
import com.eagle.EagleBankService.dto.UserResponse;
import com.eagle.EagleBankService.entity.UserEntity;
import com.eagle.EagleBankService.exception.ForbiddenException;
import com.eagle.EagleBankService.exception.NotFoundException;
import com.eagle.EagleBankService.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String FULL_NAME = "Joe Bloggs";
    private static final String EMAIL = "Joebloggs@test.com";
    private static final String PASSWORD = "password123456";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    void createUser_shouldCreateInTheRepository_andReturnUserResponse() {
        UserRequest request = new UserRequest(FULL_NAME, EMAIL, PASSWORD);
        String hashedPassword = "hashedPassword123";

        UserEntity savedUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName(FULL_NAME)
                .email(EMAIL)
                .password(hashedPassword)
                .build();

        when(passwordEncoder.encode(PASSWORD)).thenReturn(hashedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(request);

        assertThat(response.getFullName()).isEqualTo(FULL_NAME);
        assertThat(response.getEmail()).isEqualTo(EMAIL);
        assertThat(response.getId()).isNotNull();
        verify(passwordEncoder).encode(PASSWORD);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    public void updateUser_shouldUpdateUser_whenUserOwnsAccount() {
        UUID userId = UUID.randomUUID();
        String oldEmail = "oldemail@test.com";
        UserEntity user = UserEntity.builder()
                .id(userId)
                .email(oldEmail)
                .fullName("Old Name")
                .password("Old-password")
                .build();

        UpdateUserRequest updateRequest = new UpdateUserRequest(FULL_NAME, EMAIL, PASSWORD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserResponse result = userService.updateUser(userId, updateRequest, oldEmail);

        assertThat(result.getFullName()).isEqualTo(FULL_NAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void updateUser_shouldThrowNotFound_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        String oldEmail = "oldemail@test.com";
        UserEntity user = UserEntity.builder()
                .id(userId)
                .email(oldEmail)
                .fullName("Old Name")
                .password("Old-password")
                .build();

        UpdateUserRequest updateRequest = new UpdateUserRequest(FULL_NAME, EMAIL, PASSWORD);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(userId, updateRequest, oldEmail);
        });
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldThrowForbidden_whenUserDoesNotOwnAccount() {
        UUID userId = UUID.randomUUID();
        String oldEmail = "oldemail@test.com";
        UserEntity user = UserEntity.builder()
                .id(userId)
                .email(oldEmail)
                .fullName("Old Name")
                .password("Old-password")
                .build();

        UpdateUserRequest updateRequest = new UpdateUserRequest(FULL_NAME, EMAIL, PASSWORD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertThrows(ForbiddenException.class, () -> {
            userService.updateUser(userId, updateRequest, EMAIL);
        });
        verify(userRepository, never()).save(any());
    }


}


