package com.example.agrisense.service;

import com.example.agrisense.entity.Crop;
import com.example.agrisense.entity.Device;
import com.example.agrisense.entity.SensorData;
import com.example.agrisense.entity.dto.ThresholdDTO;
import com.example.agrisense.util.ThresholdHelper;
import org.springframework.stereotype.Service;

@Service
public class MockAiService implements AiService {
    @Override
    public String getAdvice(SensorData sensorData, Crop crop, String growthStage) {
        ThresholdDTO thresholds = ThresholdHelper.getThresholds(crop, growthStage);
        return generateAdvice(sensorData, crop, thresholds);
    }

    @Override
    public String getAdviceWithDevice(SensorData sensorData, Crop crop, Device device) {
        String growthStage = device != null ? device.getGrowthStage() : null;
        ThresholdDTO thresholds = ThresholdHelper.getThresholds(crop, growthStage);
        return generateAdvice(sensorData, crop, thresholds);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    private String generateAdvice(SensorData sensorData, Crop crop, ThresholdDTO thresholds) {
        StringBuilder advice = new StringBuilder();
        double temp = sensorData.getTemperature();
        double humidity = sensorData.getHumidity();

        advice.append("【").append(crop != null ? crop.getName() : "通用作物").append("】\n");
        advice.append("当前温度: ").append(temp).append("°C, 湿度: ").append(humidity).append("%\n\n");

        if (crop != null) {
            if (temp < thresholds.getMinTemp()) {
                double diff = thresholds.getMinTemp() - temp;
                advice.append("⚠️ 温度过低 ").append(String.format("%.1f", diff)).append("°C，建议关闭通风口或开启保温设备\n");
            } else if (temp > thresholds.getMaxTemp()) {
                double diff = temp - thresholds.getMaxTemp();
                advice.append("⚠️ 温度过高 ").append(String.format("%.1f", diff)).append("°C，建议立即开启侧窗通风或遮阳网\n");
            } else if (temp >= thresholds.getOptimalTempMin() && temp <= thresholds.getOptimalTempMax()) {
                advice.append("✅ 温度适宜（").append(thresholds.getOptimalTempMin()).append("-")
                        .append(thresholds.getOptimalTempMax()).append("°C）\n");
            } else if (temp < thresholds.getOptimalTempMin()) {
                advice.append("➡️ 温度略低，建议适当升温\n");
            } else {
                advice.append("➡️ 温度略高，建议适当通风\n");
            }

            if (humidity < thresholds.getMinHumidity()) {
                double diff = thresholds.getMinHumidity() - humidity;
                advice.append("⚠️ 湿度过低 ").append(String.format("%.1f", diff)).append("%，建议喷雾或浇水\n");
            } else if (humidity > thresholds.getMaxHumidity()) {
                double diff = humidity - thresholds.getMaxHumidity();
                advice.append("⚠️ 湿度过高 ").append(String.format("%.1f", diff)).append("%，注意通风排湿，预防病害\n");
            } else if (humidity >= thresholds.getOptimalHumidityMin()
                    && humidity <= thresholds.getOptimalHumidityMax()) {
                advice.append("✅ 湿度适宜（").append(thresholds.getOptimalHumidityMin()).append("-")
                        .append(thresholds.getOptimalHumidityMax()).append("%）\n");
            } else if (humidity < thresholds.getOptimalHumidityMin()) {
                advice.append("➡️ 湿度略低，建议增加湿度\n");
            } else {
                advice.append("➡️ 湿度略高，建议适当通风\n");
            }

            if (crop.getSpecialNotes() != null && !crop.getSpecialNotes().isEmpty()) {
                advice.append("\n📝 注意: ").append(crop.getSpecialNotes());
            }
        } else {
            if (temp > 35) {
                advice.append("⚠️ 温度偏高，建议开启通风\n");
            } else if (temp < 15) {
                advice.append("⚠️ 温度偏低，建议关闭通风口\n");
            }
            if (humidity > 85) {
                advice.append("⚠️ 湿度偏高，注意通风排湿\n");
            } else if (humidity < 40) {
                advice.append("⚠️ 湿度偏低，建议适当增湿\n");
            }
        }

        return advice.toString();
    }
}
