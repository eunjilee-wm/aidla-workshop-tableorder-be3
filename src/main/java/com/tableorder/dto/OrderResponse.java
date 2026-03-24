package com.tableorder.dto;

import com.tableorder.entity.OrderItem;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private Long id;
    private Long sessionId;
    private Long storeId;
    private Integer tableNumber;
    private String status;
    private Integer totalAmount;
    private String rejectionReason;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
