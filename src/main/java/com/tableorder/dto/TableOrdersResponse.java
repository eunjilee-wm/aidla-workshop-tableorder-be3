package com.tableorder.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TableOrdersResponse {
    private List<OrderResponse> orders;
    private Integer totalAmount;
}
