package com.tableorder.controller;

import com.tableorder.dto.CategoryMenuResponse;
import com.tableorder.entity.Store;
import com.tableorder.service.MenuService;
import com.tableorder.service.StoreService;
import com.tableorder.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "Store (고객)", description = "매장/테이블/메뉴 조회 API (인증 불필요)")
public class StoreController {
    private final StoreService storeService;
    private final TableService tableService;
    private final MenuService menuService;

    @GetMapping("/{storeId}")
    @Operation(summary = "매장 정보 조회")
    public ResponseEntity<Store> getStore(@PathVariable String storeId) {
        return ResponseEntity.ok(storeService.getStore(storeId));
    }

    @GetMapping("/{storeId}/tables/{tableNumber}")
    @Operation(summary = "테이블 유효성 검증 + 매장 메뉴 반환")
    public ResponseEntity<Map<String, Object>> getTableWithMenus(
            @PathVariable String storeId, @PathVariable Integer tableNumber) {
        Store store = storeService.getStore(storeId);
        tableService.validateTable(store.getId(), tableNumber);
        List<CategoryMenuResponse> menus = menuService.getMenusByStore(store.getId());
        return ResponseEntity.ok(Map.of("store", store, "tableNumber", tableNumber, "menus", menus));
    }

    @GetMapping("/{storeId}/menus")
    @Operation(summary = "매장 메뉴 조회 (카테고리별)")
    public ResponseEntity<List<CategoryMenuResponse>> getMenus(@PathVariable String storeId) {
        Store store = storeService.getStore(storeId);
        return ResponseEntity.ok(menuService.getMenusByStore(store.getId()));
    }
}
