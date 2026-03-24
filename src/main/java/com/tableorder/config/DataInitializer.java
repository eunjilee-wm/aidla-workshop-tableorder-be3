package com.tableorder.config;

import com.tableorder.entity.Admin;
import com.tableorder.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        long count = adminRepository.count();
        log.info("Admin count: {}", count);
        if (count == 0) {
            String encoded = passwordEncoder.encode("admin123");
            log.info("Inserting admin users with bcrypt hash");
            adminRepository.save(Admin.builder().storeId(1L).username("admin").password(encoded).loginAttempts(0).build());
            adminRepository.save(Admin.builder().storeId(2L).username("admin").password(encoded).loginAttempts(0).build());
            log.info("Admin users created successfully");
        } else {
            log.info("Admin users already exist, skipping");
        }
    }
}
