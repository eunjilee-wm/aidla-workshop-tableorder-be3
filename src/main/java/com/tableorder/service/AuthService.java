package com.tableorder.service;

import com.tableorder.dto.LoginResponse;
import com.tableorder.entity.Admin;
import com.tableorder.entity.Store;
import com.tableorder.exception.AccountLockedException;
import com.tableorder.exception.InvalidCredentialsException;
import com.tableorder.exception.StoreNotFoundException;
import com.tableorder.repository.AdminRepository;
import com.tableorder.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    private final AdminRepository adminRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse login(String storeId, String username, String password) {
        Store store = storeRepository.findByStoreId(storeId)
                .orElseThrow(() -> new StoreNotFoundException("Store not found: " + storeId));

        Admin admin = adminRepository.findByStoreIdAndUsername(store.getId(), username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (admin.getLockedUntil() != null && admin.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new AccountLockedException("Account is locked until " + admin.getLockedUntil());
        }

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            int attempts = admin.getLoginAttempts() != null ? admin.getLoginAttempts() : 0;
            admin.setLoginAttempts(attempts + 1);
            if (admin.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                admin.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            }
            adminRepository.save(admin);
            throw new InvalidCredentialsException("Invalid credentials");
        }

        admin.setLoginAttempts(0);
        admin.setLockedUntil(null);
        adminRepository.save(admin);

        String token = jwtUtil.generateToken(admin.getId(), store.getId(), username);
        return LoginResponse.builder().token(token).storeId(storeId).storeName(store.getName()).build();
    }
}
