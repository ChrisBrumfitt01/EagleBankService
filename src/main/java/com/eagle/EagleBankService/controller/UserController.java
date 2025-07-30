package com.eagle.EagleBankService.controller;

import com.eagle.EagleBankService.dto.UpdateUserRequest;
import com.eagle.EagleBankService.dto.UserRequest;
import com.eagle.EagleBankService.dto.UserResponse;
import com.eagle.EagleBankService.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        log.info("CreateUser request received");
        UserResponse response = userService.createUser(request);
        log.info("CreateUser request successfully completed");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID userId,
                                                   @Valid @RequestBody UpdateUserRequest request,
                                                   Authentication authentication) {
        log.info("UpdateUser request received for user ID: {}", userId);
        String authenticatedEmail = authentication.getName();
        UserResponse response = userService.updateUser(userId, request, authenticatedEmail);
        log.info("UpdateUser request successfully completed for user ID: {}", userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId,
                                           Authentication authentication) {
        log.info("DeleteUser request received for user ID, {}", userId);
        String authenticatedEmail = authentication.getName();
        userService.deleteUser(userId, authenticatedEmail);
        log.info("DeleteUser request successfully completed for user ID, {}", userId);
        return ResponseEntity.ok().build();
    }

}
