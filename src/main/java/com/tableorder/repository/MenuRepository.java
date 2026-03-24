package com.tableorder.repository;

import com.tableorder.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByCategoryIdOrderByDisplayOrder(Long categoryId);
    List<Menu> findByStoreIdOrderByDisplayOrder(Long storeId);

    @Query("SELECT MAX(m.displayOrder) FROM Menu m WHERE m.categoryId = :categoryId")
    Optional<Integer> findMaxDisplayOrderByCategoryId(Long categoryId);
}
