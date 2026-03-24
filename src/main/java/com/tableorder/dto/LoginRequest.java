package com.tableorder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    @NotBlank private String storeId;
    @NotBlank private String username;
    @NotBlank private String password;
}
