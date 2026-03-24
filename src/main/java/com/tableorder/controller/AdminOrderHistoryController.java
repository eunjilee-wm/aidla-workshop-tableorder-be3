package com.tableorder.controller;

import com.tableorder.dto.OrderHistoryResponse;
import com.tableorder.service.OrderHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/tables")
@RequiredArgsConstructor
@Tag(name = "OrderHistory (관리자)", description = "과거 주문 내역 API")
public class AdminOrderHistoryController {
    private final OrderHistoryService orderHistoryService;

    @GetMapping("/{tableNumber}/history")
    @Operation(summary = "과거 주문 내역 조회")
    public ResponseEntity<List<OrderHistoryResponse>> getHistory(
            @RequestAttribute("storeId") Long storeId,
            @PathVariable Integer tableNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return ResponseEntity.ok(orderHistoryService.getHistory(storeId, tableNumber, dateFrom, dateTo));
    }
}
