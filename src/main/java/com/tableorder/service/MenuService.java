package com.tableorder.service;

import com.tableorder.dto.*;
import com.tableorder.entity.Category;
import com.tableorder.entity.Menu;
import com.tableorder.exception.InvalidFileException;
import com.tableorder.exception.MenuNotFoundException;
import com.tableorder.repository.CategoryRepository;
import com.tableorder.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif");

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public List<CategoryMenuResponse> getMenusByStore(Long storeId) {
        return categoryRepository.findByStoreIdOrderByDisplayOrder(storeId).stream()
                .map(cat -> CategoryMenuResponse.builder()
                        .categoryId(cat.getId())
                        .categoryName(cat.getName())
                        .displayOrder(cat.getDisplayOrder())
                        .menus(menuRepository.findByCategoryIdOrderByDisplayOrder(cat.getId()))
                        .build())
                .toList();
    }

    public Menu getMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException("Menu not found: " + menuId));
    }

    @Transactional
    public Menu createMenu(Long storeId, MenuCreateRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Menu name is required");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        int nextOrder = request.getDisplayOrder() != null ? request.getDisplayOrder()
                : menuRepository.findMaxDisplayOrderByCategoryId(request.getCategoryId()).orElse(0) + 1;

        Menu menu = Menu.builder()
                .storeId(storeId).categoryId(request.getCategoryId())
                .name(request.getName()).price(request.getPrice())
                .description(request.getDescription()).displayOrder(nextOrder)
                .build();
        return menuRepository.save(menu);
    }

    @Transactional
    public Menu updateMenu(Long menuId, Long storeId, MenuUpdateRequest request) {
        Menu menu = findMenuByIdAndStore(menuId, storeId);
        if (request.getName() != null) menu.setName(request.getName());
        if (request.getPrice() != null) menu.setPrice(request.getPrice());
        if (request.getDescription() != null) menu.setDescription(request.getDescription());
        if (request.getCategoryId() != null) menu.setCategoryId(request.getCategoryId());
        return menuRepository.save(menu);
    }

    @Transactional
    public void deleteMenu(Long menuId, Long storeId) {
        Menu menu = findMenuByIdAndStore(menuId, storeId);
        Long categoryId = menu.getCategoryId();
        menuRepository.delete(menu);

        List<Menu> remaining = menuRepository.findByCategoryIdOrderByDisplayOrder(categoryId);
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setDisplayOrder(i + 1);
        }
        menuRepository.saveAll(remaining);
    }

    @Transactional
    public void updateMenuOrder(Long storeId, List<MenuOrderRequest> request) {
        for (MenuOrderRequest item : request) {
            Menu menu = findMenuByIdAndStore(item.getMenuId(), storeId);
            menu.setDisplayOrder(item.getDisplayOrder());
            menuRepository.save(menu);
        }
    }

    @Transactional
    public String uploadImage(Long menuId, Long storeId, MultipartFile file) {
        Menu menu = findMenuByIdAndStore(menuId, storeId);
        validateImageFile(file);
        String url = fileStorageService.store(file);
        menu.setImageUrl(url);
        menuRepository.save(menu);
        return url;
    }

    private Menu findMenuByIdAndStore(Long menuId, Long storeId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException("Menu not found: " + menuId));
        if (!menu.getStoreId().equals(storeId)) {
            throw new MenuNotFoundException("Menu not found in store");
        }
        return menu;
    }

    private void validateImageFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new InvalidFileException("File name is required");
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new InvalidFileException("Allowed formats: jpg, jpeg, png, gif");
        }
    }
}
