package com.tableorder.repository;

import com.tableorder.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
    List<OrderEntity> findByStoreIdOrderByCreatedAtDesc(Long storeId);
    List<OrderEntity> findByStoreIdAndStatusOrderByCreatedAtDesc(Long storeId, String status);
}
