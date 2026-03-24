package com.tableorder.service;

import com.tableorder.dto.OrderHistoryResponse;
import com.tableorder.entity.OrderHistory;
import com.tableorder.entity.OrderHistoryItem;
import com.tableorder.repository.OrderHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderHistoryServiceTest {

    @Mock private OrderHistoryRepository orderHistoryRepository;
    @InjectMocks private OrderHistoryService orderHistoryService;

    @Test
    @DisplayName("TC-026: 과거 주문 내역 조회")
    void getHistory_success() {
        OrderHistoryItem item = OrderHistoryItem.builder().menuName("김치찌개").quantity(1).unitPrice(9000).build();
        OrderHistory h1 = OrderHistory.builder().id(1L).storeId(1L).tableNumber(1).originalOrderId(1L)
                .status("COMPLETED").totalAmount(9000).orderedAt(LocalDateTime.now().minusHours(2))
                .sessionCompletedAt(LocalDateTime.now().minusHours(1)).items(List.of(item)).build();
        OrderHistory h2 = OrderHistory.builder().id(2L).storeId(1L).tableNumber(1).originalOrderId(2L)
                .status("COMPLETED").totalAmount(8000).orderedAt(LocalDateTime.now().minusDays(1))
                .sessionCompletedAt(LocalDateTime.now().minusDays(1)).items(List.of()).build();
        OrderHistory h3 = OrderHistory.builder().id(3L).storeId(1L).tableNumber(1).originalOrderId(3L)
                .status("ACCEPTED").totalAmount(5000).orderedAt(LocalDateTime.now().minusDays(2))
                .sessionCompletedAt(LocalDateTime.now().minusDays(2)).items(List.of()).build();

        when(orderHistoryRepository.findByStoreIdAndTableNumberOrderByOrderedAtDesc(1L, 1))
                .thenReturn(List.of(h1, h2, h3));

        List<OrderHistoryResponse> result = orderHistoryService.getHistory(1L, 1, null, null);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getTotalAmount()).isEqualTo(9000);
        assertThat(result.get(0).getItems()).hasSize(1);
    }

    @Test
    @DisplayName("TC-027: 날짜 필터링 조회")
    void getHistory_dateFilter() {
        LocalDate from = LocalDate.of(2026, 3, 19);
        LocalDate to = LocalDate.of(2026, 3, 20);

        OrderHistory h1 = OrderHistory.builder().id(1L).storeId(1L).tableNumber(1).originalOrderId(1L)
                .status("COMPLETED").totalAmount(9000).orderedAt(LocalDateTime.of(2026, 3, 19, 12, 0))
                .sessionCompletedAt(LocalDateTime.of(2026, 3, 19, 13, 0)).items(List.of()).build();

        when(orderHistoryRepository.findByStoreIdAndTableNumberAndOrderedAtBetweenOrderByOrderedAtDesc(
                eq(1L), eq(1), any(), any())).thenReturn(List.of(h1));

        List<OrderHistoryResponse> result = orderHistoryService.getHistory(1L, 1, from, to);

        assertThat(result).hasSize(1);
    }
}
