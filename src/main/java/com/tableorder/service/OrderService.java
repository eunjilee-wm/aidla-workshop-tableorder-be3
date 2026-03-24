package com.tableorder.service;

import com.tableorder.dto.*;
import com.tableorder.entity.*;
import com.tableorder.exception.*;
import com.tableorder.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Map<String, Set<String>> VALID_TRANSITIONS = Map.of(
            "PENDING", Set.of("ACCEPTED", "REJECTED"),
            "ACCEPTED", Set.of("PREPARING"),
            "PREPARING", Set.of("COMPLETED")
    );
    private static final Set<String> BILLABLE_STATUSES = Set.of("ACCEPTED", "PREPARING", "COMPLETED");

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SessionService sessionService;
    private final MenuService menuService;
    private final StoreService storeService;
    private final TableService tableService;

    @Transactional
    public OrderResponse createOrder(String storeId, Integer tableNumber, OrderCreateRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidOrderException("Order must have at least one item");
        }

        Store store = storeService.getStore(storeId);
        tableService.validateTable(store.getId(), tableNumber);
        Session session = sessionService.getOrCreateSession(store.getId(), tableNumber);

        int totalAmount = 0;
        OrderEntity order = OrderEntity.builder()
                .sessionId(session.getId()).storeId(store.getId()).tableNumber(tableNumber)
                .status("PENDING").totalAmount(0).build();
        order = orderRepository.save(order);

        for (OrderCreateRequest.OrderItemRequest itemReq : request.getItems()) {
            Menu menu = menuService.getMenu(itemReq.getMenuId());
            int itemTotal = menu.getPrice() * itemReq.getQuantity();
            totalAmount += itemTotal;
            orderItemRepository.save(OrderItem.builder()
                    .orderId(order.getId()).menuId(menu.getId())
                    .menuName(menu.getName()).unitPrice(menu.getPrice())
                    .quantity(itemReq.getQuantity()).build());
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return toResponse(order, items);
    }

    public TableOrdersResponse getOrdersByTable(Long storeId, Integer tableNumber) {
        Session session = sessionService.getOrCreateSession(storeId, tableNumber);
        List<OrderEntity> orders = orderRepository.findBySessionIdOrderByCreatedAtAsc(session.getId());

        int totalAmount = orders.stream()
                .filter(o -> BILLABLE_STATUSES.contains(o.getStatus()))
                .mapToInt(OrderEntity::getTotalAmount).sum();

        return TableOrdersResponse.builder()
                .orders(orders.stream().map(o -> toResponse(o, o.getItems())).toList())
                .totalAmount(totalAmount).build();
    }

    public List<OrderResponse> getOrdersByStore(Long storeId, String status) {
        List<OrderEntity> orders = (status != null && !status.isBlank())
                ? orderRepository.findByStoreIdAndStatusOrderByCreatedAtDesc(storeId, status)
                : orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId);
        return orders.stream().map(o -> toResponse(o, o.getItems())).toList();
    }

    @Transactional
    public OrderResponse approveOrder(Long orderId, Long storeId) {
        OrderEntity order = findOrderByIdAndStore(orderId, storeId);
        validateTransition(order.getStatus(), "ACCEPTED");
        order.setStatus("ACCEPTED");
        return toResponse(orderRepository.save(order), order.getItems());
    }

    @Transactional
    public OrderResponse rejectOrder(Long orderId, Long storeId, String reason) {
        OrderEntity order = findOrderByIdAndStore(orderId, storeId);
        validateTransition(order.getStatus(), "REJECTED");
        order.setStatus("REJECTED");
        order.setRejectionReason(reason);
        return toResponse(orderRepository.save(order), order.getItems());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Long storeId, String status) {
        OrderEntity order = findOrderByIdAndStore(orderId, storeId);
        validateTransition(order.getStatus(), status);
        order.setStatus(status);
        return toResponse(orderRepository.save(order), order.getItems());
    }

    @Transactional
    public void deleteOrder(Long orderId, Long storeId) {
        OrderEntity order = findOrderByIdAndStore(orderId, storeId);
        orderRepository.delete(order);
    }

    private OrderEntity findOrderByIdAndStore(Long orderId, Long storeId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        if (!order.getStoreId().equals(storeId)) {
            throw new OrderNotFoundException("Order not found in store");
        }
        return order;
    }

    private void validateTransition(String from, String to) {
        Set<String> allowed = VALID_TRANSITIONS.get(from);
        if (allowed == null || !allowed.contains(to)) {
            throw new InvalidOrderStateException("Invalid transition: " + from + " → " + to);
        }
    }

    private OrderResponse toResponse(OrderEntity order, List<OrderItem> items) {
        return OrderResponse.builder()
                .id(order.getId()).sessionId(order.getSessionId()).storeId(order.getStoreId())
                .tableNumber(order.getTableNumber()).status(order.getStatus())
                .totalAmount(order.getTotalAmount()).rejectionReason(order.getRejectionReason())
                .items(items).createdAt(order.getCreatedAt()).updatedAt(order.getUpdatedAt())
                .build();
    }
}
