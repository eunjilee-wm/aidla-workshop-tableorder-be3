package com.tableorder.service;

import com.tableorder.dto.LoginResponse;
import com.tableorder.entity.Admin;
import com.tableorder.entity.Store;
import com.tableorder.exception.AccountLockedException;
import com.tableorder.exception.InvalidCredentialsException;
import com.tableorder.repository.AdminRepository;
import com.tableorder.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AdminRepository adminRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private AuthService authService;

    private Store store;
    private Admin admin;

    @BeforeEach
    void setUp() {
        store = Store.builder().id(1L).storeId("store-001").name("맛있는 식당").build();
        admin = Admin.builder().id(1L).storeId(1L).username("admin").password("encoded").loginAttempts(0).build();
    }

    @Test
    @DisplayName("TC-001: 유효한 자격증명으로 로그인 성공 + login_attempts 리셋")
    void login_success_and_reset_attempts() {
        admin.setLoginAttempts(2);
        when(storeRepository.findByStoreId("store-001")).thenReturn(Optional.of(store));
        when(adminRepository.findByStoreIdAndUsername(1L, "admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtUtil.generateToken(1L, 1L, "admin")).thenReturn("jwt-token");

        LoginResponse response = authService.login("store-001", "admin", "password");

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getStoreId()).isEqualTo("store-001");
        assertThat(admin.getLoginAttempts()).isEqualTo(0);
        verify(adminRepository).save(admin);
    }

    @Test
    @DisplayName("TC-002: 잘못된 비밀번호로 로그인 실패 + login_attempts 증가")
    void login_fail_wrong_password() {
        when(storeRepository.findByStoreId("store-001")).thenReturn(Optional.of(store));
        when(adminRepository.findByStoreIdAndUsername(1L, "admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.login("store-001", "admin", "wrong"))
                .isInstanceOf(InvalidCredentialsException.class);
        assertThat(admin.getLoginAttempts()).isEqualTo(1);
        verify(adminRepository).save(admin);
    }

    @Test
    @DisplayName("TC-003: 5회 실패 후 계정 잠금")
    void login_fail_lock_after_5_attempts() {
        admin.setLoginAttempts(4);
        when(storeRepository.findByStoreId("store-001")).thenReturn(Optional.of(store));
        when(adminRepository.findByStoreIdAndUsername(1L, "admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.login("store-001", "admin", "wrong"))
                .isInstanceOf(InvalidCredentialsException.class);
        assertThat(admin.getLoginAttempts()).isEqualTo(5);
        assertThat(admin.getLockedUntil()).isNotNull();
        assertThat(admin.getLockedUntil()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("TC-004: 잠금 상태에서 로그인 시도")
    void login_fail_account_locked() {
        admin.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        when(storeRepository.findByStoreId("store-001")).thenReturn(Optional.of(store));
        when(adminRepository.findByStoreIdAndUsername(1L, "admin")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> authService.login("store-001", "admin", "password"))
                .isInstanceOf(AccountLockedException.class);
    }
}
