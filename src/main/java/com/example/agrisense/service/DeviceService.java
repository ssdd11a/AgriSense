package com.example.agrisense.service;

import com.example.agrisense.entity.Device;
import com.example.agrisense.entity.dto.DeviceDTO;
import com.example.agrisense.exception.ResourceNotFoundException;
import com.example.agrisense.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Optional<Device> getDeviceById(String id) {
        return deviceRepository.findById(id);
    }

    public Optional<Device> getDeviceByApiKey(String apiKey) {
        return deviceRepository.findByApiKey(apiKey);
    }

    @Transactional
    public Device createDevice(DeviceDTO deviceDTO) {
        Device device = new Device();
        updateDeviceFromDTO(device, deviceDTO);
        if (device.getId() == null || device.getId().isEmpty()) {
            device.setId(generateDeviceId());
        }
        device.setCreatedAt(LocalDateTime.now());
        device.setIsActive(true);
        if (deviceDTO.getApiKey() == null || deviceDTO.getApiKey().isEmpty()) {
            device.setApiKey(generateApiKey());
        }
        return deviceRepository.save(device);
    }

    @Transactional
    public Device updateDevice(String id, DeviceDTO deviceDTO) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("设备不存在: " + id));
        updateDeviceFromDTO(device, deviceDTO);
        return deviceRepository.save(device);
    }

    @Transactional
    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    @Transactional
    public void deleteDevice(String id) {
        if (!deviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("设备不存在: " + id);
        }
        deviceRepository.deleteById(id);
    }

    public void updateLastActive(String deviceId) {
        getDeviceById(deviceId).ifPresent(device -> {
            device.setLastActive(LocalDateTime.now());
            deviceRepository.save(device);
        });
    }

    private void updateDeviceFromDTO(Device device, DeviceDTO deviceDTO) {
        if (deviceDTO.getId() != null && !deviceDTO.getId().isEmpty()) {
            device.setId(deviceDTO.getId());
        }
        device.setName(deviceDTO.getName());
        device.setLocation(deviceDTO.getLocation());
        device.setCropId(deviceDTO.getCropId());
        device.setGrowthStage(deviceDTO.getGrowthStage());
        if (deviceDTO.getApiKey() != null && !deviceDTO.getApiKey().isEmpty()) {
            device.setApiKey(deviceDTO.getApiKey());
        }
    }

    private String generateDeviceId() {
        return "device_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateApiKey() {
        return "DEVICE_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
