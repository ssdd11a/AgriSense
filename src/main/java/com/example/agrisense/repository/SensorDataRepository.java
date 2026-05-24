package com.example.agrisense.repository;

import com.example.agrisense.entity.SensorData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    Optional<SensorData> findFirstByDeviceIdOrderByTimestampDesc(String deviceId);

    Page<SensorData> findByDeviceIdOrderByTimestampDesc(String deviceId, Pageable pageable);

    @Query(value = "SELECT s FROM SensorData s WHERE s.deviceId = :deviceId ORDER BY s.timestamp DESC")
    Optional<SensorData> findLatestByDeviceId(String deviceId);
}
