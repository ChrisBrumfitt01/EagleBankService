package com.eagle.EagleBankService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @NotBlank(message = "'fullName' must be provided")
    private String fullName;

    @NotBlank(message = "'email' must be provided")
    @Email(message = "'email' must be a valid email address")
    private String email;

    @NotBlank(message = "'password' must be provided")
    private String password;
}
