package com.tableorder.repository;

import com.tableorder.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByStoreIdAndTableNumberAndStatus(Long storeId, Integer tableNumber, String status);
}
