package com.example.agrisense.controller;

import com.example.agrisense.entity.Crop;
import com.example.agrisense.entity.GrowthStage;
import com.example.agrisense.entity.dto.CropDTO;
import com.example.agrisense.service.CropService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
@CrossOrigin(origins = "*")
@Tag(name = "作物管理", description = "作物类型管理API")
public class CropController {
    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @GetMapping
    @Operation(summary = "获取所有作物")
    public ResponseEntity<List<Crop>> getAllCrops() {
        return ResponseEntity.ok(cropService.getAllCrops());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取作物")
    public ResponseEntity<Crop> getCropById(@PathVariable Long id) {
        return cropService.getCropById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/stages")
    @Operation(summary = "获取作物的所有生长阶段")
    public ResponseEntity<List<GrowthStage>> getStagesByCropId(@PathVariable Long id) {
        return ResponseEntity.ok(cropService.getStagesByCropId(id));
    }

    @PostMapping
    @Operation(summary = "创建新作物")
    public ResponseEntity<Crop> createCrop(@Valid @RequestBody CropDTO cropDTO) {
        return ResponseEntity.ok(cropService.createCrop(cropDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新作物")
    public ResponseEntity<Crop> updateCrop(@PathVariable Long id, @Valid @RequestBody CropDTO cropDTO) {
        return ResponseEntity.ok(cropService.updateCrop(id, cropDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除作物")
    public ResponseEntity<Void> deleteCrop(@PathVariable Long id) {
        cropService.deleteCrop(id);
        return ResponseEntity.noContent().build();
    }
}
