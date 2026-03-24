package com.tableorder.controller;

import com.tableorder.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tables")
@RequiredArgsConstructor
@Tag(name = "Session (관리자)", description = "테이블 세션 관리 API")
public class AdminSessionController {
    private final SessionService sessionService;

    @PostMapping("/{tableNumber}/complete")
    @Operation(summary = "이용 완료 (세션 종료)")
    public ResponseEntity<Void> completeSession(@RequestAttribute("storeId") Long storeId,
                                                @PathVariable Integer tableNumber) {
        sessionService.completeSession(storeId, tableNumber);
        return ResponseEntity.noContent().build();
    }
}
