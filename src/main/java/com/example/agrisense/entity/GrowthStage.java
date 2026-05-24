package com.example.agrisense.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "growth_stages")
@Data
public class GrowthStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id")
    @JsonIgnoreProperties({ "stages" })
    private Crop crop;

    private String name;

    private String description;

    @Column(name = "min_temp")
    private Double minTemp;

    @Column(name = "optimal_temp_min")
    private Double optimalTempMin;

    @Column(name = "optimal_temp_max")
    private Double optimalTempMax;

    @Column(name = "max_temp")
    private Double maxTemp;

    @Column(name = "min_humidity")
    private Double minHumidity;

    @Column(name = "optimal_humidity_min")
    private Double optimalHumidityMin;

    @Column(name = "optimal_humidity_max")
    private Double optimalHumidityMax;

    @Column(name = "max_humidity")
    private Double maxHumidity;

    @Column(name = "stage_order")
    private Integer order;
}
