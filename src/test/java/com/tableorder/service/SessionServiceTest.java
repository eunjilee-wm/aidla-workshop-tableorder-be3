package com.tableorder.service;

import com.tableorder.entity.*;
import com.tableorder.exception.SessionNotFoundException;
import com.tableorder.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock private SessionRepository sessionRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private OrderHistoryRepository orderHistoryRepository;
    @Mock private OrderHistoryItemRepository orderHistoryItemRepository;
    @InjectMocks private SessionService sessionService;

    @Test
    @DisplayName("TC-024: 활성 세션 없을 때 새 세션 생성")
    void getOrCreateSession_createNew() {
        when(sessionRepository.findByStoreIdAndTableNumberAndStatus(1L, 1, "ACTIVE"))
                .thenReturn(Optional.empty());
        when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> {
            Session s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        Session result = sessionService.getOrCreateSession(1L, 1);

        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getStoreId()).isEqualTo(1L);
        assertThat(result.getTableNumber()).isEqualTo(1);
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    @DisplayName("TC-025: 세션 종료 - 주문 이력 이동")
    void completeSession_moveToHistory() {
        Session session = Session.builder().id(1L).storeId(1L).tableNumber(1).status("ACTIVE").build();
        OrderItem item1 = OrderItem.builder().id(1L).orderId(1L).menuId(1L).menuName("김치찌개").quantity(1).unitPrice(9000).build();
        OrderItem item2 = OrderItem.builder().id(2L).orderId(1L).menuId(2L).menuName("콜라").quantity(2).unitPrice(2000).build();
        OrderEntity order1 = OrderEntity.builder().id(1L).sessionId(1L).storeId(1L).tableNumber(1)
                .status("COMPLETED").totalAmount(13000).items(List.of(item1, item2))
                .createdAt(LocalDateTime.now()).build();
        OrderItem item3 = OrderItem.builder().id(3L).orderId(2L).menuId(1L).menuName("된장찌개").quantity(1).unitPrice(8000).build();
        OrderEntity order2 = OrderEntity.builder().id(2L).sessionId(1L).storeId(1L).tableNumber(1)
                .status("ACCEPTED").totalAmount(8000).items(List.of(item3))
                .createdAt(LocalDateTime.now()).build();

        when(sessionRepository.findByStoreIdAndTableNumberAndStatus(1L, 1, "ACTIVE"))
                .thenReturn(Optional.of(session));
        when(orderRepository.findBySessionIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(order1, order2));
        when(orderHistoryRepository.save(any(OrderHistory.class))).thenAnswer(inv -> {
            OrderHistory h = inv.getArgument(0);
            h.setId(1L);
            return h;
        });

        sessionService.completeSession(1L, 1);

        verify(orderHistoryRepository, times(2)).save(any(OrderHistory.class));
        verify(orderHistoryItemRepository, times(3)).save(any(OrderHistoryItem.class));
        verify(orderRepository, times(2)).delete(any(OrderEntity.class));
        assertThat(session.getStatus()).isEqualTo("COMPLETED");
        assertThat(session.getCompletedAt()).isNotNull();
    }
}
