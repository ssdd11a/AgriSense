package com.example.agrisense.controller;

import com.example.agrisense.entity.Device;
import com.example.agrisense.entity.SensorData;
import com.example.agrisense.service.DeviceService;
import com.example.agrisense.service.SensorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SensorController {
    private static final Logger logger = LoggerFactory.getLogger(SensorController.class);

    private final SensorService sensorService;
    private final DeviceService deviceService;

    public SensorController(SensorService sensorService, DeviceService deviceService) {
        this.sensorService = sensorService;
        this.deviceService = deviceService;
    }

    @PostMapping("/sensor")
    public ResponseEntity<?> save(@RequestBody SensorDataRequest request) {
        try {
            logger.info("收到传感器数据 - deviceId: {}, temp: {}, hum: {}",
                    request.deviceId, request.temperature, request.humidity);

            SensorData saved = sensorService.save(
                    request.deviceId != null ? request.deviceId : "1",
                    request.temperature,
                    request.humidity);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("保存传感器数据失败", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/sensor/latest")
    public ResponseEntity<Optional<SensorData>> getLatest(@RequestParam(required = false) String deviceId) {
        if (deviceId != null) {
            return ResponseEntity.ok(sensorService.getLatestByDeviceId(deviceId));
        }
        return ResponseEntity.ok(sensorService.getLatest());
    }

    @GetMapping("/sensor/advice")
    public ResponseEntity<String> getAdvice(@RequestParam(required = false) String deviceId) {
        return ResponseEntity.ok(sensorService.getAdviceForDevice(deviceId != null ? deviceId : "1"));
    }

    @GetMapping("/sensor/history")
    public ResponseEntity<Page<SensorData>> getHistory(
            @RequestParam(required = false) String deviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sensorService.getHistoryByDeviceId(deviceId != null ? deviceId : "1", pageable));
    }

    public static class SensorDataRequest {
        public String deviceId;
        @NotNull
        public Double temperature;
        @NotNull
        public Double humidity;
    }

    public static class UpdateDeviceRequest {
        public String name;
        public Long cropId;
        public String growthStage;
    }
}
