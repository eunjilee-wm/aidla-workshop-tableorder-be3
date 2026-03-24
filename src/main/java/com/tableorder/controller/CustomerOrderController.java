package com.tableorder.controller;

import com.tableorder.dto.*;
import com.tableorder.entity.Store;
import com.tableorder.service.OrderService;
import com.tableorder.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores/{storeId}/tables/{tableNumber}/orders")
@RequiredArgsConstructor
@Tag(name = "Order (고객)", description = "고객 주문 API (인증 불필요)")
public class CustomerOrderController {
    private final OrderService orderService;
    private final StoreService storeService;

    @PostMapping
    @Operation(summary = "주문 생성")
    public ResponseEntity<OrderResponse> createOrder(
            @PathVariable String storeId, @PathVariable Integer tableNumber,
            @Valid @RequestBody OrderCreateRequest request) {
        return ResponseEntity.ok(orderService.createOrder(storeId, tableNumber, request));
    }

    @GetMapping
    @Operation(summary = "테이블 현재 주문 조회")
    public ResponseEntity<TableOrdersResponse> getOrders(
            @PathVariable String storeId, @PathVariable Integer tableNumber) {
        Store store = storeService.getStore(storeId);
        return ResponseEntity.ok(orderService.getOrdersByTable(store.getId(), tableNumber));
    }
}
