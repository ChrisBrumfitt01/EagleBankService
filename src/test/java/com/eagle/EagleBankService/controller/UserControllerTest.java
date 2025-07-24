package com.eagle.EagleBankService.controller;

import com.eagle.EagleBankService.config.TestSecurityConfig;
import com.eagle.EagleBankService.dto.UserRequest;
import com.eagle.EagleBankService.dto.UserResponse;
import com.eagle.EagleBankService.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    private static final String FULL_NAME = "Joe Bloggs";
    private static final String EMAIL = "Joebloggs@test.com";
    private static final String PASSWORD = "password123456";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createUser_shouldReturn201AndUserResponse_whenPayloadIsValid() throws Exception {
        UserRequest request = new UserRequest(FULL_NAME, EMAIL, PASSWORD);
        UserResponse response = new UserResponse(UUID.randomUUID(), FULL_NAME, EMAIL);

        when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value(FULL_NAME))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    void createUser_shouldReturn400_whenMissingFullName() throws Exception {
        UserRequest invalidRequest = new UserRequest("", EMAIL, PASSWORD);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("'fullName' must be provided;"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void createUser_shouldReturn400_whenMissingEmail() throws Exception {
        UserRequest invalidRequest = new UserRequest(FULL_NAME, "", PASSWORD);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("'email' must be provided;"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void createUser_shouldReturn400_whenEmailIsInvalidFormat() throws Exception {
        UserRequest invalidRequest = new UserRequest(FULL_NAME, "Invalid", PASSWORD);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("'email' must be a valid email address;"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void createUser_shouldReturn400_whenMissingPassword() throws Exception {
        UserRequest invalidRequest = new UserRequest(FULL_NAME, EMAIL, "");

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("'password' must be provided;"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

}
