package com.example.agrisense.repository;

import com.example.agrisense.entity.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    Page<Alert> findByDeviceIdOrderByCreatedAtDesc(String deviceId, Pageable pageable);
}
