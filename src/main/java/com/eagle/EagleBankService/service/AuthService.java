package com.eagle.EagleBankService.service;

import com.eagle.EagleBankService.entity.UserEntity;
import com.eagle.EagleBankService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserService userService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userService.findUserByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("No user found with the given email address")
        );
        return new User(user.getEmail(), user.getPassword(), Collections.emptyList());
    }
}
