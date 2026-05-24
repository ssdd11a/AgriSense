package com.example.agrisense.repository;

import com.example.agrisense.entity.GrowthStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrowthStageRepository extends JpaRepository<GrowthStage, Long> {
    List<GrowthStage> findByCropIdOrderByOrderAsc(Long cropId);
    void deleteByCropId(Long cropId);
}
