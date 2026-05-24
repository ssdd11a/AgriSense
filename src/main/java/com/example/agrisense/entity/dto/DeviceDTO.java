package com.example.agrisense.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceDTO {
    private String id;

    @NotBlank(message = "设备名称不能为空")
    private String name;

    private String location;

    private String apiKey;

    private Long cropId;

    private String growthStage;
}
