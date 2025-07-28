package com.eagle.EagleBankService.service.transaction;

import com.eagle.EagleBankService.dto.TransactionRequest;
import com.eagle.EagleBankService.dto.CreatedTransactionResponse;
import com.eagle.EagleBankService.entity.AccountEntity;
import com.eagle.EagleBankService.entity.TransactionEntity;
import com.eagle.EagleBankService.entity.UserEntity;
import com.eagle.EagleBankService.exception.ForbiddenException;
import com.eagle.EagleBankService.exception.NotFoundException;
import com.eagle.EagleBankService.exception.UnauthorizedException;
import com.eagle.EagleBankService.exception.UnprocessableException;
import com.eagle.EagleBankService.model.TransactionType;
import com.eagle.EagleBankService.repository.AccountRepository;
import com.eagle.EagleBankService.repository.TransactionRepository;
import com.eagle.EagleBankService.service.AccountService;
import com.eagle.EagleBankService.service.UserService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WithdrawalStrategyTest {

    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final String EMAIL = "joebloggs@test.com";

    @Mock private UserService userService;
    @Mock private AccountRepository accountRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private AccountService accountService;

    @Captor
    private ArgumentCaptor<AccountEntity> accountCaptor;
    @Captor private ArgumentCaptor<TransactionEntity> transactionCaptor;

    @InjectMocks
    private WithdrawalStrategy withdrawalStrategy;

    @Test
    public void process_shouldUpdateAccount_andCreateTransactionSuccessfully() {
        UserEntity user = buildUser();
        AccountEntity account = buildAccount(user);
        TransactionEntity savedTransaction = buildTransaction();

        when(userService.findUserByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(accountService.getAccountAndVerifyOwner(ACCOUNT_ID, user.getId())).thenReturn(account);
        when(transactionRepository.save(any())).thenReturn(savedTransaction);

        CreatedTransactionResponse response = withdrawalStrategy.process(ACCOUNT_ID, EMAIL,
                new TransactionRequest(new BigDecimal(100), TransactionType.WITHDRAWAL));

        verify(accountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue().getBalance()).isEqualTo(new BigDecimal(400));

        verify(transactionRepository).save(transactionCaptor.capture());
        TransactionEntity actualTransaction = transactionCaptor.getValue();
        assertThat(actualTransaction.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(actualTransaction.getAmount()).isEqualTo(new BigDecimal(100));

        assertThat(response.getTransactionId()).isEqualTo(savedTransaction.getId());
    }

    @Test
    public void process_shouldThrowUnprocessableException_whenThereAreInsufficientFunds() {
        UserEntity user = buildUser();
        AccountEntity account = buildAccount(user);
        TransactionEntity savedTransaction = buildTransaction();

        when(userService.findUserByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(accountService.getAccountAndVerifyOwner(ACCOUNT_ID, user.getId())).thenReturn(account);

        assertThrows(UnprocessableException.class, () -> {
            withdrawalStrategy.process(ACCOUNT_ID, EMAIL,
                    new TransactionRequest(new BigDecimal(999999), TransactionType.WITHDRAWAL));
        });
    }

    @Test
    public void process_shouldThrowUnauthorizedException_whenUserNotFound() {
        when(userService.findUserByEmail(EMAIL)).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> {
            withdrawalStrategy.process(ACCOUNT_ID, EMAIL, new TransactionRequest(new BigDecimal(100), TransactionType.WITHDRAWAL));
        });
    }


    private UserEntity buildUser() {
        return UserEntity.builder()
                .id(UUID.randomUUID())
                .email(EMAIL)
                .build();
    }

    private AccountEntity buildAccount(UserEntity user) {
        return AccountEntity.builder()
                .id(UUID.randomUUID())
                .accountNumber("12345678")
                .balance(new BigDecimal(500))
                .user(user)
                .build();

    }

    private TransactionEntity buildTransaction() {
        return TransactionEntity.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal(100))
                .build();

    }



}
