package com.tableorder.service;

import com.tableorder.dto.*;
import com.tableorder.entity.Category;
import com.tableorder.entity.Menu;
import com.tableorder.exception.InvalidFileException;
import com.tableorder.exception.MenuNotFoundException;
import com.tableorder.repository.CategoryRepository;
import com.tableorder.repository.MenuRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private FileStorageService fileStorageService;
    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("TC-009: 매장 전체 메뉴 카테고리별 조회")
    void getMenusByStore_success() {
        Category c1 = Category.builder().id(1L).storeId(1L).name("메인").displayOrder(1).build();
        Category c2 = Category.builder().id(2L).storeId(1L).name("사이드").displayOrder(2).build();
        Menu m1 = Menu.builder().id(1L).categoryId(1L).storeId(1L).name("김치찌개").price(9000).displayOrder(1).build();
        Menu m2 = Menu.builder().id(2L).categoryId(1L).storeId(1L).name("된장찌개").price(8000).displayOrder(2).build();
        Menu m3 = Menu.builder().id(3L).categoryId(2L).storeId(1L).name("계란말이").price(5000).displayOrder(1).build();

        when(categoryRepository.findByStoreIdOrderByDisplayOrder(1L)).thenReturn(List.of(c1, c2));
        when(menuRepository.findByCategoryIdOrderByDisplayOrder(1L)).thenReturn(List.of(m1, m2));
        when(menuRepository.findByCategoryIdOrderByDisplayOrder(2L)).thenReturn(List.of(m3));

        List<CategoryMenuResponse> result = menuService.getMenusByStore(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategoryName()).isEqualTo("메인");
        assertThat(result.get(0).getMenus()).hasSize(2);
        assertThat(result.get(1).getMenus()).hasSize(1);
    }

    @Test
    @DisplayName("TC-010: 메뉴 생성 성공 (display_order 자동 부여)")
    void createMenu_success() {
        Category cat = Category.builder().id(1L).storeId(1L).name("메인").build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(menuRepository.findMaxDisplayOrderByCategoryId(1L)).thenReturn(Optional.of(2));
        when(menuRepository.save(any(Menu.class))).thenAnswer(inv -> {
            Menu m = inv.getArgument(0);
            m.setId(10L);
            return m;
        });

        MenuCreateRequest req = new MenuCreateRequest("새메뉴", 10000, "설명", 1L, null);
        Menu result = menuService.createMenu(1L, req);

        assertThat(result.getDisplayOrder()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("새메뉴");
    }

    @Test
    @DisplayName("TC-028: 메뉴 생성 시 필수 필드 누락 검증")
    void createMenu_missingName() {
        MenuCreateRequest req = new MenuCreateRequest("", 10000, null, 1L, null);

        assertThatThrownBy(() -> menuService.createMenu(1L, req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("TC-029: 메뉴 생성 시 가격 0 이하 검증")
    void createMenu_invalidPrice() {
        MenuCreateRequest req = new MenuCreateRequest("메뉴", 0, null, 1L, null);

        assertThatThrownBy(() -> menuService.createMenu(1L, req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("TC-011: 메뉴 수정 성공")
    void updateMenu_success() {
        Menu existing = Menu.builder().id(1L).storeId(1L).categoryId(1L).name("김치찌개").price(9000).build();
        when(menuRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(menuRepository.save(any(Menu.class))).thenAnswer(inv -> inv.getArgument(0));

        MenuUpdateRequest req = new MenuUpdateRequest("김치찌개 특", 10000, "업그레이드", null);
        Menu result = menuService.updateMenu(1L, 1L, req);

        assertThat(result.getName()).isEqualTo("김치찌개 특");
        assertThat(result.getPrice()).isEqualTo(10000);
    }

    @Test
    @DisplayName("TC-012: 메뉴 삭제 시 순서 재정렬")
    void deleteMenu_reorder() {
        Menu m1 = Menu.builder().id(1L).storeId(1L).categoryId(1L).displayOrder(1).build();
        Menu m2 = Menu.builder().id(2L).storeId(1L).categoryId(1L).displayOrder(2).build();
        Menu m3 = Menu.builder().id(3L).storeId(1L).categoryId(1L).displayOrder(3).build();

        when(menuRepository.findById(2L)).thenReturn(Optional.of(m2));
        when(menuRepository.findByCategoryIdOrderByDisplayOrder(1L)).thenReturn(List.of(m1, m3));

        menuService.deleteMenu(2L, 1L);

        verify(menuRepository).delete(m2);
        assertThat(m1.getDisplayOrder()).isEqualTo(1);
        assertThat(m3.getDisplayOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("TC-013: 메뉴 순서 변경")
    void updateMenuOrder_success() {
        Menu m1 = Menu.builder().id(1L).storeId(1L).displayOrder(1).build();
        Menu m2 = Menu.builder().id(2L).storeId(1L).displayOrder(2).build();

        when(menuRepository.findById(1L)).thenReturn(Optional.of(m1));
        when(menuRepository.findById(2L)).thenReturn(Optional.of(m2));

        List<MenuOrderRequest> req = List.of(new MenuOrderRequest(1L, 2), new MenuOrderRequest(2L, 1));
        menuService.updateMenuOrder(1L, req);

        assertThat(m1.getDisplayOrder()).isEqualTo(2);
        assertThat(m2.getDisplayOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("TC-014: 이미지 업로드 성공")
    void uploadImage_success() {
        Menu menu = Menu.builder().id(1L).storeId(1L).build();
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(fileStorageService.store(any())).thenReturn("/uploads/image.jpg");
        when(menuRepository.save(any(Menu.class))).thenAnswer(inv -> inv.getArgument(0));

        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "data".getBytes());
        String url = menuService.uploadImage(1L, 1L, file);

        assertThat(url).isEqualTo("/uploads/image.jpg");
        assertThat(menu.getImageUrl()).isEqualTo("/uploads/image.jpg");
    }

    @Test
    @DisplayName("TC-031: 지원하지 않는 이미지 형식 업로드 실패")
    void uploadImage_invalidFormat() {
        Menu menu = Menu.builder().id(1L).storeId(1L).build();
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));

        MockMultipartFile file = new MockMultipartFile("image", "test.bmp", "image/bmp", "data".getBytes());

        assertThatThrownBy(() -> menuService.uploadImage(1L, 1L, file))
                .isInstanceOf(InvalidFileException.class);
    }
}
