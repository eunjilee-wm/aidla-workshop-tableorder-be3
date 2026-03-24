package com.tableorder.service;

import com.tableorder.dto.OrderHistoryResponse;
import com.tableorder.entity.OrderHistory;
import com.tableorder.repository.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;

    public List<OrderHistoryResponse> getHistory(Long storeId, Integer tableNumber, LocalDate dateFrom, LocalDate dateTo) {
        List<OrderHistory> histories;
        if (dateFrom != null && dateTo != null) {
            histories = orderHistoryRepository.findByStoreIdAndTableNumberAndOrderedAtBetweenOrderByOrderedAtDesc(
                    storeId, tableNumber,
                    dateFrom.atStartOfDay(),
                    dateTo.atTime(LocalTime.MAX));
        } else {
            histories = orderHistoryRepository.findByStoreIdAndTableNumberOrderByOrderedAtDesc(storeId, tableNumber);
        }
        return histories.stream().map(this::toResponse).toList();
    }

    private OrderHistoryResponse toResponse(OrderHistory h) {
        return OrderHistoryResponse.builder()
                .id(h.getId()).originalOrderId(h.getOriginalOrderId())
                .status(h.getStatus()).totalAmount(h.getTotalAmount())
                .rejectionReason(h.getRejectionReason())
                .orderedAt(h.getOrderedAt()).sessionCompletedAt(h.getSessionCompletedAt())
                .items(h.getItems().stream().map(i -> OrderHistoryResponse.OrderHistoryItemResponse.builder()
                        .menuName(i.getMenuName()).quantity(i.getQuantity()).unitPrice(i.getUnitPrice())
                        .build()).toList())
                .build();
    }
}
