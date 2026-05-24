package com.example.agrisense.util;

import com.example.agrisense.entity.Crop;
import com.example.agrisense.entity.GrowthStage;
import com.example.agrisense.entity.dto.ThresholdDTO;

import java.util.List;

public class ThresholdHelper {

    public static ThresholdDTO getThresholds(Crop crop, String growthStageName) {
        if (crop == null || crop.getStages() == null || crop.getStages().isEmpty()) {
            return ThresholdDTO.createDefault();
        }

        GrowthStage targetStage = findStage(crop.getStages(), growthStageName);
        if (targetStage != null) {
            return new ThresholdDTO(
                    targetStage.getMinTemp(),
                    targetStage.getOptimalTempMin(),
                    targetStage.getOptimalTempMax(),
                    targetStage.getMaxTemp(),
                    targetStage.getMinHumidity(),
                    targetStage.getOptimalHumidityMin(),
                    targetStage.getOptimalHumidityMax(),
                    targetStage.getMaxHumidity()
            );
        }

        GrowthStage firstStage = crop.getStages().get(0);
        return new ThresholdDTO(
                firstStage.getMinTemp(),
                firstStage.getOptimalTempMin(),
                firstStage.getOptimalTempMax(),
                firstStage.getMaxTemp(),
                firstStage.getMinHumidity(),
                firstStage.getOptimalHumidityMin(),
                firstStage.getOptimalHumidityMax(),
                firstStage.getMaxHumidity()
        );
    }

    private static GrowthStage findStage(List<GrowthStage> stages, String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return stages.stream()
                .filter(s -> name.equals(s.getName()))
                .findFirst()
                .orElse(null);
    }
}
