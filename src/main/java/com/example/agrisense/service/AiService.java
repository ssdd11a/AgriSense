package com.example.agrisense.service;

import com.example.agrisense.entity.Crop;
import com.example.agrisense.entity.Device;
import com.example.agrisense.entity.SensorData;

public interface AiService {
    String getAdvice(SensorData sensorData, Crop crop, String growthStage);

    String getAdviceWithDevice(SensorData sensorData, Crop crop, Device device);

    boolean isAvailable();
}
