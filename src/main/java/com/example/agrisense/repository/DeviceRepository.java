package com.example.agrisense.repository;

import com.example.agrisense.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    Optional<Device> findByApiKey(String apiKey);

    @Modifying
    @Query("UPDATE Device d SET d.cropId = :newCropId WHERE d.cropId = :oldCropId")
    int updateCropId(@Param("oldCropId") Long oldCropId, @Param("newCropId") Long newCropId);
}
