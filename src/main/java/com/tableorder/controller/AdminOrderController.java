package com.tableorder.controller;

import com.tableorder.dto.*;
import com.tableorder.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Order (관리자)", description = "주문 관리 API")
public class AdminOrderController {
    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "매장 주문 목록 조회")
    public ResponseEntity<List<OrderResponse>> getOrders(@RequestAttribute("storeId") Long storeId,
                                                         @RequestParam(required = false) String status) {
        return ResponseEntity.ok(orderService.getOrdersByStore(storeId, status));
    }

    @PutMapping("/{orderId}/approve")
    @Operation(summary = "주문 승인")
    public ResponseEntity<OrderResponse> approveOrder(@RequestAttribute("storeId") Long storeId,
                                                      @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.approveOrder(orderId, storeId));
    }

    @PutMapping("/{orderId}/reject")
    @Operation(summary = "주문 거절")
    public ResponseEntity<OrderResponse> rejectOrder(@RequestAttribute("storeId") Long storeId,
                                                     @PathVariable Long orderId, @RequestBody(required = false) RejectRequest request) {
        String reason = request != null ? request.getReason() : null;
        return ResponseEntity.ok(orderService.rejectOrder(orderId, storeId, reason));
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "주문 상태 변경")
    public ResponseEntity<OrderResponse> updateStatus(@RequestAttribute("storeId") Long storeId,
                                                      @PathVariable Long orderId, @RequestBody OrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, storeId, request.getStatus()));
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "주문 삭제")
    public ResponseEntity<Void> deleteOrder(@RequestAttribute("storeId") Long storeId, @PathVariable Long orderId) {
        orderService.deleteOrder(orderId, storeId);
        return ResponseEntity.noContent().build();
    }
}
