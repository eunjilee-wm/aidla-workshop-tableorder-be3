package com.tableorder.service;

import com.tableorder.entity.StoreTable;
import com.tableorder.exception.TableNotFoundException;
import com.tableorder.repository.StoreTableRepository;
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
class TableServiceTest {

    @Mock private StoreTableRepository storeTableRepository;
    @InjectMocks private TableService tableService;

    @Test
    @DisplayName("TC-007: 유효한 매장+테이블 조합 검증 성공")
    void validateTable_success() {
        StoreTable table = StoreTable.builder().id(1L).storeId(1L).tableNumber(1).build();
        when(storeTableRepository.findByStoreIdAndTableNumber(1L, 1)).thenReturn(Optional.of(table));

        StoreTable result = tableService.validateTable(1L, 1);

        assertThat(result.getTableNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("TC-008: 존재하지 않는 테이블 검증 실패")
    void validateTable_notFound() {
        when(storeTableRepository.findByStoreIdAndTableNumber(1L, 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tableService.validateTable(1L, 99))
                .isInstanceOf(TableNotFoundException.class);
    }
}
