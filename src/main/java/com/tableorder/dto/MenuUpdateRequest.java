package com.tableorder.dto;

import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MenuUpdateRequest {
    private String name;
    @Positive private Integer price;
    private String description;
    private Long categoryId;
}
