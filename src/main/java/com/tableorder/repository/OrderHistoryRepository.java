package com.tableorder.repository;

import com.tableorder.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findByStoreIdAndTableNumberOrderByOrderedAtDesc(Long storeId, Integer tableNumber);
    List<OrderHistory> findByStoreIdAndTableNumberAndOrderedAtBetweenOrderByOrderedAtDesc(
            Long storeId, Integer tableNumber, LocalDateTime from, LocalDateTime to);
}
