package com.tableorder.service;

import com.tableorder.dto.*;
import com.tableorder.entity.*;
import com.tableorder.exception.*;
import com.tableorder.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private SessionService sessionService;
    @Mock private MenuService menuService;
    @Mock private StoreService storeService;
    @Mock private TableService tableService;
    @InjectMocks private OrderService orderService;

    private Store store;
    private Session session;

    @BeforeEach
    void setUp() {
        store = Store.builder().id(1L).storeId("store-001").name("맛있는 식당").build();
        session = Session.builder().id(1L).storeId(1L).tableNumber(1).status("ACTIVE").build();
    }

    @Test
    @DisplayName("TC-015: 주문 생성 성공 (새 세션 자동 생성)")
    void createOrder_newSession() {
        when(storeService.getStore("store-001")).thenReturn(store);
        when(tableService.validateTable(1L, 1)).thenReturn(StoreTable.builder().build());
        when(sessionService.getOrCreateSession(1L, 1)).thenReturn(session);
        Menu menu = Menu.builder().id(1L).storeId(1L).name("김치찌개").price(9000).build();
        when(menuService.getMenu(1L)).thenReturn(menu);
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(inv -> {
            OrderEntity o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        OrderCreateRequest req = new OrderCreateRequest(List.of(
                new OrderCreateRequest.OrderItemRequest(1L, 2)));
        OrderResponse result = orderService.createOrder("store-001", 1, req);

        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getTotalAmount()).isEqualTo(18000);
        verify(sessionService).getOrCreateSession(1L, 1);
    }

    @Test
    @DisplayName("TC-016: 주문 생성 성공 (기존 세션에 추가)")
    void createOrder_existingSession() {
        when(storeService.getStore("store-001")).thenReturn(store);
        when(tableService.validateTable(1L, 1)).thenReturn(StoreTable.builder().build());
        when(sessionService.getOrCreateSession(1L, 1)).thenReturn(session);
        Menu menu = Menu.builder().id(1L).storeId(1L).name("김치찌개").price(9000).build();
        when(menuService.getMenu(1L)).thenReturn(menu);
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(inv -> {
            OrderEntity o = inv.getArgument(0);
            o.setId(2L);
            return o;
        });

        OrderCreateRequest req = new OrderCreateRequest(List.of(
                new OrderCreateRequest.OrderItemRequest(1L, 1)));
        OrderResponse result = orderService.createOrder("store-001", 1, req);

        assertThat(result.getSessionId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("TC-030: 빈 주문 항목으로 주문 생성 실패")
    void createOrder_emptyItems() {
        OrderCreateRequest req = new OrderCreateRequest(List.of());

        assertThatThrownBy(() -> orderService.createOrder("store-001", 1, req))
                .isInstanceOf(InvalidOrderException.class);
    }

    @Test
    @DisplayName("TC-017: 테이블별 현재 주문 조회 + 총 주문액")
    void getOrdersByTable_withTotalAmount() {
        OrderEntity o1 = OrderEntity.builder().id(1L).sessionId(1L).storeId(1L).tableNumber(1)
                .status("ACCEPTED").totalAmount(10000).items(new ArrayList<>()).createdAt(LocalDateTime.now()).build();
        OrderEntity o2 = OrderEntity.builder().id(2L).sessionId(1L).storeId(1L).tableNumber(1)
                .status("PENDING").totalAmount(5000).items(new ArrayList<>()).createdAt(LocalDateTime.now()).build();

        when(sessionService.getOrCreateSession(1L, 1)).thenReturn(session);
        when(orderRepository.findBySessionIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(o1, o2));

        TableOrdersResponse result = orderService.getOrdersByTable(1L, 1);

        assertThat(result.getOrders()).hasSize(2);
        assertThat(result.getTotalAmount()).isEqualTo(10000); // ACCEPTED만 합산
    }

    @Test
    @DisplayName("TC-018: 주문 승인 (PENDING → ACCEPTED)")
    void approveOrder_success() {
        OrderEntity order = OrderEntity.builder().id(1L).storeId(1L).status("PENDING").items(new ArrayList<>()).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse result = orderService.approveOrder(1L, 1L);

        assertThat(result.getStatus()).isEqualTo("ACCEPTED");
    }

    @Test
    @DisplayName("TC-019: 주문 거절 + 사유")
    void rejectOrder_withReason() {
        OrderEntity order = OrderEntity.builder().id(1L).storeId(1L).status("PENDING").items(new ArrayList<>()).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse result = orderService.rejectOrder(1L, 1L, "재료 소진");

        assertThat(result.getStatus()).isEqualTo("REJECTED");
        assertThat(result.getRejectionReason()).isEqualTo("재료 소진");
    }

    @Test
    @DisplayName("TC-020: 잘못된 상태 전이 거부 (REJECTED → ACCEPTED)")
    void approveOrder_invalidState() {
        OrderEntity order = OrderEntity.builder().id(1L).storeId(1L).status("REJECTED").build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.approveOrder(1L, 1L))
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    @DisplayName("TC-021: 주문 상태 변경 (ACCEPTED → PREPARING)")
    void updateOrderStatus_success() {
        OrderEntity order = OrderEntity.builder().id(1L).storeId(1L).status("ACCEPTED").items(new ArrayList<>()).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse result = orderService.updateOrderStatus(1L, 1L, "PREPARING");

        assertThat(result.getStatus()).isEqualTo("PREPARING");
    }

    @Test
    @DisplayName("TC-022: 주문 삭제 (관리자 직권)")
    void deleteOrder_success() {
        OrderEntity order = OrderEntity.builder().id(1L).storeId(1L).status("ACCEPTED").build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L, 1L);

        verify(orderRepository).delete(order);
    }

    @Test
    @DisplayName("TC-023: 매장별 주문 목록 조회 (상태 필터)")
    void getOrdersByStore_withFilter() {
        OrderEntity o1 = OrderEntity.builder().id(1L).storeId(1L).status("PENDING").totalAmount(9000)
                .items(new ArrayList<>()).createdAt(LocalDateTime.now()).build();
        when(orderRepository.findByStoreIdAndStatusOrderByCreatedAtDesc(1L, "PENDING")).thenReturn(List.of(o1));

        List<OrderResponse> result = orderService.getOrdersByStore(1L, "PENDING");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
    }
}
