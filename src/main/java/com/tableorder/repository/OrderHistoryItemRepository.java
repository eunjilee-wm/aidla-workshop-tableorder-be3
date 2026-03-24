package com.tableorder.repository;

import com.tableorder.entity.OrderHistoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryItemRepository extends JpaRepository<OrderHistoryItem, Long> {
}
