package com.tableorder.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MenuCreateRequest {
    @NotBlank private String name;
    @NotNull @Positive private Integer price;
    private String description;
    @NotNull private Long categoryId;
    private Integer displayOrder;
}
