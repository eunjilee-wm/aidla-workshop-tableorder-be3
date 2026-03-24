package com.tableorder.service;

import com.tableorder.entity.*;
import com.tableorder.exception.SessionNotFoundException;
import com.tableorder.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderHistoryItemRepository orderHistoryItemRepository;

    public Session getOrCreateSession(Long storeId, Integer tableNumber) {
        return sessionRepository.findByStoreIdAndTableNumberAndStatus(storeId, tableNumber, "ACTIVE")
                .orElseGet(() -> sessionRepository.save(
                        Session.builder().storeId(storeId).tableNumber(tableNumber).status("ACTIVE").build()));
    }

    @Transactional
    public void completeSession(Long storeId, Integer tableNumber) {
        Session session = sessionRepository.findByStoreIdAndTableNumberAndStatus(storeId, tableNumber, "ACTIVE")
                .orElseThrow(() -> new SessionNotFoundException("Active session not found"));

        LocalDateTime completedAt = LocalDateTime.now();
        List<OrderEntity> orders = orderRepository.findBySessionIdOrderByCreatedAtAsc(session.getId());

        for (OrderEntity order : orders) {
            OrderHistory history = orderHistoryRepository.save(OrderHistory.builder()
                    .storeId(order.getStoreId()).tableNumber(order.getTableNumber())
                    .originalOrderId(order.getId()).status(order.getStatus())
                    .totalAmount(order.getTotalAmount()).rejectionReason(order.getRejectionReason())
                    .orderedAt(order.getCreatedAt()).sessionCompletedAt(completedAt)
                    .build());

            for (OrderItem item : order.getItems()) {
                orderHistoryItemRepository.save(OrderHistoryItem.builder()
                        .orderHistoryId(history.getId()).menuName(item.getMenuName())
                        .quantity(item.getQuantity()).unitPrice(item.getUnitPrice())
                        .build());
            }
            orderRepository.delete(order);
        }

        session.setStatus("COMPLETED");
        session.setCompletedAt(completedAt);
        sessionRepository.save(session);
    }
}
