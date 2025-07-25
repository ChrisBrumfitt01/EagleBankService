package com.eagle.EagleBankService.service;

import com.eagle.EagleBankService.dto.AccountRequest;
import com.eagle.EagleBankService.dto.AccountResponse;
import com.eagle.EagleBankService.entity.AccountEntity;
import com.eagle.EagleBankService.entity.UserEntity;
import com.eagle.EagleBankService.exception.ForbiddenException;
import com.eagle.EagleBankService.exception.NotFoundException;
import com.eagle.EagleBankService.exception.UnauthorizedException;
import com.eagle.EagleBankService.repository.AccountRepository;
import com.eagle.EagleBankService.util.AccountNumberGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final String EMAIL = "joe@test.com";
    private static final String ACCOUNT_NUMBER = "12345678";
    private static final String ACCOUNT_TYPE = "Savings";

    @Mock private AccountRepository accountRepository;
    @Mock private UserService userService;
    @Mock private AccountNumberGenerator accountNumberGenerator;
    @InjectMocks AccountService accountService;
    @Captor
    ArgumentCaptor<AccountEntity> captor;
    @Test
    public void createAccount_shouldSaveAccount_whenInputIsValid() {
        UserEntity user = buildUser();

        AccountRequest accountRequest = new AccountRequest(ACCOUNT_TYPE);
        AccountEntity savedAccount = AccountEntity.builder().build();
        when(userService.findUserByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(accountNumberGenerator.generateUniqueAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(accountRepository.save(any())).thenReturn(savedAccount);

        accountService.createAccount(accountRequest, EMAIL);

        verify(accountRepository).save(captor.capture());
        AccountEntity actual = captor.getValue();
        assertThat(actual.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
        assertThat(actual.getAccountType()).isEqualTo(ACCOUNT_TYPE);
        assertThat(actual.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void createAccount_throwsUnauthorized_whenUserNotFound() {
        AccountRequest accountRequest = new AccountRequest(ACCOUNT_TYPE);
        when(userService.findUserByEmail(EMAIL)).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> {
            accountService.createAccount(accountRequest, EMAIL);
        });
        verify(accountRepository, never()).save(any());
    }

    @Test
    public void getAccount_shouldRetrieveAndReturnAccount() {
        UserEntity user = buildUser();
        AccountEntity savedAccount = AccountEntity.builder()
                .id(ACCOUNT_ID)
                .user(user)
                .accountType(ACCOUNT_TYPE)
                .accountNumber(ACCOUNT_NUMBER)
                .balance(BigDecimal.TEN)
                .build();

        when(userService.findUserByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.ofNullable(savedAccount));

        AccountResponse response = accountService.getAccount(ACCOUNT_ID, EMAIL);
        assertThat(response.getId()).isEqualTo(ACCOUNT_ID);
        assertThat(response.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
        assertThat(response.getAccountType()).isEqualTo(ACCOUNT_TYPE);
        assertThat(response.getBalance()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void getAccount_shouldThrowUnauthorized_whenUserNotFound() {
        when(userService.findUserByEmail(EMAIL)).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> {
            accountService.getAccount(ACCOUNT_ID, EMAIL);
        });
    }

    @Test
    public void getAccount_shouldThrowNotFoundException_whenAccountIDNotFound() {
        UserEntity user = buildUser();

        when(userService.findUserByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> {
            accountService.getAccount(ACCOUNT_ID, EMAIL);
        });
    }

    @Test
    public void getAccount_shouldThrowForbiddenException_whenUserIdDoesNotMatch() {
        UserEntity authorizedUser = buildUser();
        UserEntity otherUser = UserEntity.builder().id(UUID.randomUUID()).build();
        AccountEntity savedAccount = AccountEntity.builder()
                .id(ACCOUNT_ID)
                .user(otherUser)
                .accountType(ACCOUNT_TYPE)
                .accountNumber(ACCOUNT_NUMBER)
                .balance(BigDecimal.TEN)
                .build();

        when(userService.findUserByEmail(EMAIL)).thenReturn(Optional.of(authorizedUser));
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.ofNullable(savedAccount));
        assertThrows(ForbiddenException.class, () -> {
            accountService.getAccount(ACCOUNT_ID, EMAIL);
        });
    }

    private UserEntity buildUser() {
        return UserEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .build();
    }


}
