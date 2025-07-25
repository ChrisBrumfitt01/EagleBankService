package com.eagle.EagleBankService.util;

import com.eagle.EagleBankService.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountNumberGeneratorTest {

    @Mock private AccountRepository accountRepository;
    @InjectMocks
    private AccountNumberGenerator generator;

    @Test
    public void generateUniqueAccountNumber_shouldReturn8DigitNumber_whenFirstIsUnique() {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        String result = generator.generateUniqueAccountNumber();

        assertThat(result).hasSize(8)
                .matches("\\d{8}");
        verify(accountRepository, atLeastOnce()).findByAccountNumber(result);
    }


}
