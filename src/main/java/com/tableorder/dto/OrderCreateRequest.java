package com.tableorder.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderCreateRequest {
    @NotEmpty private List<OrderItemRequest> items;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull private Long menuId;
        @NotNull @Positive private Integer quantity;
    }
}
