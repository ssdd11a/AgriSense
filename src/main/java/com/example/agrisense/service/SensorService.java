package com.example.agrisense.service;

import com.example.agrisense.entity.Crop;
import com.example.agrisense.entity.Device;
import com.example.agrisense.entity.SensorData;
import com.example.agrisense.repository.SensorDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class SensorService {
    private final SensorDataRepository repository;
    private final DeviceService deviceService;
    private final CropService cropService;
    private final AlertService alertService;
    private final DeepSeekAiService aiService;

    public SensorService(SensorDataRepository repository, DeviceService deviceService,
            CropService cropService, AlertService alertService, DeepSeekAiService aiService) {
        this.repository = repository;
        this.deviceService = deviceService;
        this.cropService = cropService;
        this.alertService = alertService;
        this.aiService = aiService;
    }

    public SensorData save(String deviceId, Double temperature, Double humidity) {
        // 校验必填参数
        if (deviceId == null || deviceId.trim().isEmpty()) {
            deviceId = "1";
        }
        if (temperature == null || humidity == null) {
            throw new IllegalArgumentException("温度和湿度不能为空");
        }

        try {
            // 自动注册设备
            if (!deviceService.getDeviceById(deviceId).isPresent()) {
                Device newDevice = new Device();
                newDevice.setId(deviceId);
                newDevice.setName("设备" + deviceId);
                newDevice.setLocation("自动注册");
                newDevice.setApiKey("AUTO_" + System.currentTimeMillis());
                newDevice.setIsActive(true);
                newDevice.setCreatedAt(java.time.LocalDateTime.now());
                deviceService.save(newDevice);
            }

            SensorData data = new SensorData(deviceId, temperature, humidity);
            SensorData saved = repository.save(data);

            deviceService.updateLastActive(deviceId);

            // 尝试创建告警，但不影响主要功能
            try {
                Optional<Device> device = deviceService.getDeviceById(deviceId);
                if (device.isPresent() && device.get().getCropId() != null) {
                    Optional<Crop> crop = cropService.getCropById(device.get().getCropId());
                    if (crop.isPresent()) {
                        alertService.checkAndCreateAlerts(saved, crop.get(), device.get());
                    }
                }
            } catch (Exception e) {
                // 告警创建失败不影响数据保存
                System.err.println("告警创建失败: " + e.getMessage());
            }

            return saved;
        } catch (Exception e) {
            throw new RuntimeException("保存传感器数据失败: " + e.getMessage(), e);
        }
    }

    public Optional<SensorData> getLatest() {
        return getLatestByDeviceId("1");
    }

    public Optional<SensorData> getLatestData() {
        return getLatest();
    }

    public Optional<SensorData> getLatestByDeviceId(String deviceId) {
        return repository.findFirstByDeviceIdOrderByTimestampDesc(deviceId);
    }

    public Page<SensorData> getHistoryByDeviceId(String deviceId, Pageable pageable) {
        return repository.findByDeviceIdOrderByTimestampDesc(deviceId, pageable);
    }

    public String getAdviceForDevice(String deviceId) {
        try {
            Optional<SensorData> latestData = getLatestByDeviceId(deviceId);
            if (latestData.isEmpty()) {
                return "暂无传感器数据，请先上报";
            }

            Optional<Device> device = deviceService.getDeviceById(deviceId);
            Crop crop = null;
            String growthStage = "通用";

            if (device.isPresent()) {
                if (device.get().getCropId() != null) {
                    crop = cropService.getCropById(device.get().getCropId()).orElse(null);
                }
                growthStage = device.get().getGrowthStage() != null ? device.get().getGrowthStage() : "通用";
            }

            if (crop == null) {
                crop = cropService.getDefaultCrop().orElse(null);
            }

            return aiService.getAdviceWithDevice(latestData.get(), crop, device.orElse(null));
        } catch (Exception e) {
            System.err.println("获取AI建议失败: " + e.getMessage());
            e.printStackTrace();
            return "AI建议暂不可用，请稍后重试。";
        }
    }
}
