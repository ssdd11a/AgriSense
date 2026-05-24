package com.example.agrisense.service;

import com.example.agrisense.entity.Crop;
import com.example.agrisense.entity.GrowthStage;
import com.example.agrisense.entity.dto.CropDTO;
import com.example.agrisense.exception.ResourceNotFoundException;
import com.example.agrisense.repository.CropRepository;
import com.example.agrisense.repository.DeviceRepository;
import com.example.agrisense.repository.GrowthStageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CropService {
    private final CropRepository cropRepository;
    private final GrowthStageRepository growthStageRepository;
    private final DeviceRepository deviceRepository;

    public CropService(CropRepository cropRepository, GrowthStageRepository growthStageRepository,
            DeviceRepository deviceRepository) {
        this.cropRepository = cropRepository;
        this.growthStageRepository = growthStageRepository;
        this.deviceRepository = deviceRepository;
    }

    public List<Crop> getAllCrops() {
        return cropRepository.findAll();
    }

    public Optional<Crop> getCropById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return cropRepository.findById(id);
    }

    public Optional<Crop> getDefaultCrop() {
        return cropRepository.findByIsDefaultTrue();
    }

    public List<GrowthStage> getStagesByCropId(Long cropId) {
        return growthStageRepository.findByCropIdOrderByOrderAsc(cropId);
    }

    @Transactional
    public Crop createCrop(CropDTO cropDTO) {
        validateCropDTO(cropDTO);

        Crop crop = new Crop();
        updateCropFromDTO(crop, cropDTO);

        if (cropDTO.getIsDefault() != null && cropDTO.getIsDefault()) {
            unsetOtherDefaultCrops();
        }

        return cropRepository.save(crop);
    }

    @Transactional
    public Crop updateCrop(Long id, CropDTO cropDTO) {
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("作物不存在: " + id));

        validateCropDTO(cropDTO);
        updateCropFromDTO(crop, cropDTO);

        if (cropDTO.getIsDefault() != null && cropDTO.getIsDefault()) {
            unsetOtherDefaultCrops();
        }

        return cropRepository.save(crop);
    }

    @Transactional
    public void deleteCrop(Long id) {
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("作物不存在: " + id));

        if (crop.getIsDefault() != null && crop.getIsDefault()) {
            throw new IllegalStateException("不能删除默认作物");
        }

        // 级联更新关联设备
        Optional<Crop> defaultCropOpt = cropRepository.findByIsDefaultTrue();
        if (defaultCropOpt.isPresent()) {
            Crop defaultCrop = defaultCropOpt.get();
            deviceRepository.updateCropId(id, defaultCrop.getId());
        }

        cropRepository.delete(crop);
    }

    private void validateCropDTO(CropDTO cropDTO) {
        if (cropDTO.getStages() != null && !cropDTO.getStages().isEmpty()) {
            for (CropDTO.GrowthStageDTO stageDTO : cropDTO.getStages()) {
                // 跳过没有名称的阶段
                if (stageDTO.getName() == null || stageDTO.getName().trim().isEmpty()) {
                    continue;
                }
                // 如果提供了温度值，则验证温度范围
                if (stageDTO.getMinTemp() != null && stageDTO.getOptimalTempMin() != null
                        && stageDTO.getOptimalTempMax() != null && stageDTO.getMaxTemp() != null) {
                    if (stageDTO.getMinTemp() > stageDTO.getOptimalTempMin()
                            || stageDTO.getOptimalTempMin() > stageDTO.getOptimalTempMax()
                            || stageDTO.getOptimalTempMax() > stageDTO.getMaxTemp()) {
                        throw new IllegalArgumentException(
                                "阶段 " + stageDTO.getName() + " 温度阈值不满足: 最低 ≤ 最适最低 ≤ 最适最高 ≤ 最高");
                    }
                }
                // 如果提供了湿度值，则验证湿度范围
                if (stageDTO.getMinHumidity() != null && stageDTO.getOptimalHumidityMin() != null
                        && stageDTO.getOptimalHumidityMax() != null && stageDTO.getMaxHumidity() != null) {
                    if (stageDTO.getMinHumidity() > stageDTO.getOptimalHumidityMin()
                            || stageDTO.getOptimalHumidityMin() > stageDTO.getOptimalHumidityMax()
                            || stageDTO.getOptimalHumidityMax() > stageDTO.getMaxHumidity()) {
                        throw new IllegalArgumentException(
                                "阶段 " + stageDTO.getName() + " 湿度阈值不满足: 最低 ≤ 最适最低 ≤ 最适最高 ≤ 最高");
                    }
                }
            }
        }
    }

    private void updateCropFromDTO(Crop crop, CropDTO cropDTO) {
        crop.setName(cropDTO.getName());
        crop.setIcon(cropDTO.getIcon() != null ? cropDTO.getIcon() : "");
        crop.setDescription(cropDTO.getDescription() != null ? cropDTO.getDescription() : "");
        crop.setSpecialNotes(cropDTO.getSpecialNotes() != null ? cropDTO.getSpecialNotes() : "");
        crop.setIsDefault(cropDTO.getIsDefault() != null ? cropDTO.getIsDefault() : false);

        // 清除现有阶段并添加新阶段
        crop.getStages().clear();
        if (cropDTO.getStages() != null && !cropDTO.getStages().isEmpty()) {
            int order = 0;
            for (CropDTO.GrowthStageDTO stageDTO : cropDTO.getStages()) {
                // 跳过没有名称的阶段
                if (stageDTO.getName() == null || stageDTO.getName().trim().isEmpty()) {
                    continue;
                }
                GrowthStage stage = new GrowthStage();
                stage.setName(stageDTO.getName());
                stage.setDescription(stageDTO.getDescription());
                stage.setMinTemp(stageDTO.getMinTemp() != null ? stageDTO.getMinTemp() : 10.0);
                stage.setOptimalTempMin(stageDTO.getOptimalTempMin() != null ? stageDTO.getOptimalTempMin() : 18.0);
                stage.setOptimalTempMax(stageDTO.getOptimalTempMax() != null ? stageDTO.getOptimalTempMax() : 28.0);
                stage.setMaxTemp(stageDTO.getMaxTemp() != null ? stageDTO.getMaxTemp() : 35.0);
                stage.setMinHumidity(stageDTO.getMinHumidity() != null ? stageDTO.getMinHumidity() : 40.0);
                stage.setOptimalHumidityMin(
                        stageDTO.getOptimalHumidityMin() != null ? stageDTO.getOptimalHumidityMin() : 60.0);
                stage.setOptimalHumidityMax(
                        stageDTO.getOptimalHumidityMax() != null ? stageDTO.getOptimalHumidityMax() : 80.0);
                stage.setMaxHumidity(stageDTO.getMaxHumidity() != null ? stageDTO.getMaxHumidity() : 90.0);
                stage.setOrder(order++);
                crop.addStage(stage);
            }
        }
    }

    private void unsetOtherDefaultCrops() {
        cropRepository.findByIsDefaultTrue().ifPresent(defaultCrop -> {
            defaultCrop.setIsDefault(false);
            cropRepository.save(defaultCrop);
        });
    }
}
