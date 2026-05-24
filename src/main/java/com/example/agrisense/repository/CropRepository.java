package com.example.agrisense.repository;

import com.example.agrisense.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    Optional<Crop> findByIsDefaultTrue();
    Optional<Crop> findByName(String name);
}
