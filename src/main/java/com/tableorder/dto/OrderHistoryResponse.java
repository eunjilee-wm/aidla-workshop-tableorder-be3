package com.tableorder.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderHistoryResponse {
    private Long id;
    private Long originalOrderId;
    private String status;
    private Integer totalAmount;
    private String rejectionReason;
    private LocalDateTime orderedAt;
    private LocalDateTime sessionCompletedAt;
    private List<OrderHistoryItemResponse> items;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderHistoryItemResponse {
        private String menuName;
        private Integer quantity;
        private Integer unitPrice;
    }
}
