package com.example.agrisense.controller;

import com.example.agrisense.entity.Device;
import com.example.agrisense.entity.dto.DeviceDTO;
import com.example.agrisense.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
@Tag(name = "设备管理", description = "设备管理API")
public class DeviceController {
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    @Operation(summary = "获取所有设备")
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取设备")
    public ResponseEntity<Device> getDeviceById(@PathVariable String id) {
        return deviceService.getDeviceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "创建设备")
    public ResponseEntity<Device> createDevice(@Valid @RequestBody DeviceDTO deviceDTO) {
        return ResponseEntity.ok(deviceService.createDevice(deviceDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新设备")
    public ResponseEntity<Device> updateDevice(@PathVariable String id, @Valid @RequestBody DeviceDTO deviceDTO) {
        return ResponseEntity.ok(deviceService.updateDevice(id, deviceDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除设备")
    public ResponseEntity<Void> deleteDevice(@PathVariable String id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}
