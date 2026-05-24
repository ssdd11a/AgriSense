package com.example.agrisense.service;

import com.example.agrisense.entity.Alert;
import com.example.agrisense.entity.Crop;
import com.example.agrisense.entity.Device;
import com.example.agrisense.entity.SensorData;
import com.example.agrisense.entity.dto.ThresholdDTO;
import com.example.agrisense.repository.AlertRepository;
import com.example.agrisense.util.ThresholdHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AlertService {
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public void checkAndCreateAlerts(SensorData sensorData, Crop crop, Device device) {
        if (crop == null) {
            return;
        }

        ThresholdDTO thresholds = ThresholdHelper.getThresholds(crop,
                device != null ? device.getGrowthStage() : null);

        double temp = sensorData.getTemperature();
        double humidity = sensorData.getHumidity();

        if (temp < thresholds.getMinTemp()) {
            createAlert(sensorData, "TEMPERATURE_LOW",
                    String.format("温度过低(%.1f°C)，最低阈值%.1f°C", temp, thresholds.getMinTemp()),
                    temp, thresholds.getMinTemp());
        } else if (temp > thresholds.getMaxTemp()) {
            createAlert(sensorData, "TEMPERATURE_HIGH",
                    String.format("温度过高(%.1f°C)，最高阈值%.1f°C", temp, thresholds.getMaxTemp()),
                    temp, thresholds.getMaxTemp());
        }

        if (humidity < thresholds.getMinHumidity()) {
            createAlert(sensorData, "HUMIDITY_LOW",
                    String.format("湿度过低(%.1f%%)，最低阈值%.1f%%", humidity, thresholds.getMinHumidity()),
                    humidity, thresholds.getMinHumidity());
        } else if (humidity > thresholds.getMaxHumidity()) {
            createAlert(sensorData, "HUMIDITY_HIGH",
                    String.format("湿度过高(%.1f%%)，最高阈值%.1f%%", humidity, thresholds.getMaxHumidity()),
                    humidity, thresholds.getMaxHumidity());
        }
    }

    public void checkAndCreateAlerts(SensorData sensorData, Crop crop) {
        checkAndCreateAlerts(sensorData, crop, null);
    }

    private void createAlert(SensorData sensorData, String alertType, String message,
            Double currentValue, Double thresholdValue) {
        Alert alert = new Alert();
        alert.setDeviceId(sensorData.getDeviceId());
        alert.setSensorDataId(sensorData.getId());
        alert.setAlertType(alertType);
        alert.setMessage(message);
        alert.setCurrentValue(currentValue);
        alert.setThresholdValue(thresholdValue);
        alert.setIsResolved(false);
        alert.setCreatedAt(LocalDateTime.now());

        alertRepository.save(alert);
        logger.info("创建告警: {}", message);
    }
}
