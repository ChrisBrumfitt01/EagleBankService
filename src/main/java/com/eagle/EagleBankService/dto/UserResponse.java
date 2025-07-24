package com.eagle.EagleBankService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResponse {
    private UUID id;
    private String fullName;
    private String email;
}
