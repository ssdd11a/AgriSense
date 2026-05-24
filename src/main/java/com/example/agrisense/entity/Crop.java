package com.example.agrisense.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "crops")
@Data
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String icon;

    private String description;

    @Column(name = "special_notes", columnDefinition = "TEXT")
    private String specialNotes;

    private Boolean isDefault;

    @OneToMany(mappedBy = "crop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({ "crop" })
    private List<GrowthStage> stages = new ArrayList<>();

    public void addStage(GrowthStage stage) {
        stages.add(stage);
        stage.setCrop(this);
    }

    public void removeStage(GrowthStage stage) {
        stages.remove(stage);
        stage.setCrop(null);
    }
}
