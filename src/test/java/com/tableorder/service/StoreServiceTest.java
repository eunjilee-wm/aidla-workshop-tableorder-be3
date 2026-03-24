package com.tableorder.service;

import com.tableorder.entity.Store;
import com.tableorder.exception.StoreNotFoundException;
import com.tableorder.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock private StoreRepository storeRepository;
    @InjectMocks private StoreService storeService;

    @Test
    @DisplayName("TC-005: 유효한 storeId로 매장 조회")
    void getStore_success() {
        Store store = Store.builder().id(1L).storeId("store-001").name("맛있는 식당").build();
        when(storeRepository.findByStoreId("store-001")).thenReturn(Optional.of(store));

        Store result = storeService.getStore("store-001");

        assertThat(result.getStoreId()).isEqualTo("store-001");
        assertThat(result.getName()).isEqualTo("맛있는 식당");
    }

    @Test
    @DisplayName("TC-006: 존재하지 않는 storeId로 조회 실패")
    void getStore_notFound() {
        when(storeRepository.findByStoreId("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storeService.getStore("invalid"))
                .isInstanceOf(StoreNotFoundException.class);
    }
}
