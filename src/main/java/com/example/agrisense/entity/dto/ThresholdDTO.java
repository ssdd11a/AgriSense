package com.example.agrisense.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThresholdDTO {
    private Double minTemp;
    private Double optimalTempMin;
    private Double optimalTempMax;
    private Double maxTemp;
    private Double minHumidity;
    private Double optimalHumidityMin;
    private Double optimalHumidityMax;
    private Double maxHumidity;

    public static ThresholdDTO createDefault() {
        return new ThresholdDTO(10.0, 18.0, 28.0, 35.0, 40.0, 60.0, 80.0, 90.0);
    }
}
