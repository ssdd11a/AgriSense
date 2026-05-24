package com.example.agrisense.entity.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CropDTO {
    private Long id;

    @NotBlank(message = "作物名称不能为空")
    private String name;

    private String icon;

    private String description;

    private String specialNotes;

    private Boolean isDefault;

    private List<GrowthStageDTO> stages = new ArrayList<>();

    @Data
    public static class GrowthStageDTO {
        private Long id;

        @NotBlank(message = "阶段名称不能为空")
        private String name;

        private String description;

        @NotNull(message = "最低温度不能为空")
        @DecimalMin(value = "-20.0", message = "温度不能低于 -20°C")
        @DecimalMax(value = "50.0", message = "温度不能高于 50°C")
        private Double minTemp;

        @NotNull(message = "最适最低温度不能为空")
        @DecimalMin(value = "-20.0", message = "温度不能低于 -20°C")
        @DecimalMax(value = "50.0", message = "温度不能高于 50°C")
        private Double optimalTempMin;

        @NotNull(message = "最适最高温度不能为空")
        @DecimalMin(value = "-20.0", message = "温度不能低于 -20°C")
        @DecimalMax(value = "50.0", message = "温度不能高于 50°C")
        private Double optimalTempMax;

        @NotNull(message = "最高温度不能为空")
        @DecimalMin(value = "-20.0", message = "温度不能低于 -20°C")
        @DecimalMax(value = "50.0", message = "温度不能高于 50°C")
        private Double maxTemp;

        @NotNull(message = "最低湿度不能为空")
        @DecimalMin(value = "0.0", message = "湿度不能低于 0%")
        @DecimalMax(value = "100.0", message = "湿度不能高于 100%")
        private Double minHumidity;

        @NotNull(message = "最适最低湿度不能为空")
        @DecimalMin(value = "0.0", message = "湿度不能低于 0%")
        @DecimalMax(value = "100.0", message = "湿度不能高于 100%")
        private Double optimalHumidityMin;

        @NotNull(message = "最适最高湿度不能为空")
        @DecimalMin(value = "0.0", message = "湿度不能低于 0%")
        @DecimalMax(value = "100.0", message = "湿度不能高于 100%")
        private Double optimalHumidityMax;

        @NotNull(message = "最高湿度不能为空")
        @DecimalMin(value = "0.0", message = "湿度不能低于 0%")
        @DecimalMax(value = "100.0", message = "湿度不能高于 100%")
        private Double maxHumidity;

        private Integer order;
    }
}
