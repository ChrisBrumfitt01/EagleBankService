package com.eagle.EagleBankService.controller;

import com.eagle.EagleBankService.config.TestSecurityConfig;
import com.eagle.EagleBankService.dto.AccountRequest;
import com.eagle.EagleBankService.dto.AccountResponse;
import com.eagle.EagleBankService.filter.AuthTokenFilter;
import com.eagle.EagleBankService.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    private static final String EMAIL = "joe@test.com";
    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final String ACCOUNT_NUMBER = "12345678";
    private static final String ACCOUNT_TYPE = "Savings";

    @Test
    void createAccount_shouldReturn201_whenValidRequestWithToken() throws Exception {
        AccountRequest request = new AccountRequest(ACCOUNT_TYPE);
        AccountResponse response = new AccountResponse(ACCOUNT_ID, ACCOUNT_TYPE, ACCOUNT_NUMBER, BigDecimal.ZERO);

        when(accountService.createAccount(any(AccountRequest.class), eq(EMAIL))).thenReturn(response);

        mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountType").value(ACCOUNT_TYPE))
                .andExpect(jsonPath("$.accountNumber").value(ACCOUNT_NUMBER))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void createAccount_shouldReturn400_whenMissingAccountType() throws Exception {
        AccountRequest request = new AccountRequest("");

        mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("'accountType' must be provided;"));
    }


}
