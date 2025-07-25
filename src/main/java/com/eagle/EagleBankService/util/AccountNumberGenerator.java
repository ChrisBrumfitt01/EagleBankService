package com.eagle.EagleBankService.util;

import com.eagle.EagleBankService.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;
@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {
    private final AccountRepository accountRepository;

    public String generateUniqueAccountNumber() {
        String number;
        do {
            number = String.valueOf(10000000 + new Random().nextInt(90000000));
        } while (accountRepository.findByAccountNumber(number).isPresent());
        return number;
    }
}
