package com.tableorder.controller;

import com.tableorder.dto.*;
import com.tableorder.entity.Menu;
import com.tableorder.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menus")
@RequiredArgsConstructor
@Tag(name = "Menu (관리자)", description = "메뉴 관리 API")
public class AdminMenuController {
    private final MenuService menuService;

    @GetMapping
    @Operation(summary = "매장 전체 메뉴 조회")
    public ResponseEntity<List<CategoryMenuResponse>> getMenus(@RequestAttribute("storeId") Long storeId) {
        return ResponseEntity.ok(menuService.getMenusByStore(storeId));
    }

    @PostMapping
    @Operation(summary = "메뉴 등록")
    public ResponseEntity<Menu> createMenu(@RequestAttribute("storeId") Long storeId,
                                           @Valid @RequestBody MenuCreateRequest request) {
        return ResponseEntity.ok(menuService.createMenu(storeId, request));
    }

    @PutMapping("/{menuId}")
    @Operation(summary = "메뉴 수정")
    public ResponseEntity<Menu> updateMenu(@RequestAttribute("storeId") Long storeId,
                                           @PathVariable Long menuId, @RequestBody MenuUpdateRequest request) {
        return ResponseEntity.ok(menuService.updateMenu(menuId, storeId, request));
    }

    @DeleteMapping("/{menuId}")
    @Operation(summary = "메뉴 삭제")
    public ResponseEntity<Void> deleteMenu(@RequestAttribute("storeId") Long storeId, @PathVariable Long menuId) {
        menuService.deleteMenu(menuId, storeId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/order")
    @Operation(summary = "메뉴 순서 변경")
    public ResponseEntity<Void> updateMenuOrder(@RequestAttribute("storeId") Long storeId,
                                                @RequestBody List<MenuOrderRequest> request) {
        menuService.updateMenuOrder(storeId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{menuId}/image")
    @Operation(summary = "메뉴 이미지 업로드")
    public ResponseEntity<String> uploadImage(@RequestAttribute("storeId") Long storeId,
                                              @PathVariable Long menuId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(menuService.uploadImage(menuId, storeId, file));
    }
}
