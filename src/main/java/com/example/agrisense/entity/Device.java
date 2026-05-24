package com.example.agrisense.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
@Data
public class Device {
    @Id
    private String id;

    private String name;

    private String apiKey;

    @Column(name = "crop_id")
    private Long cropId;

    private String growthStage;

    @Column(name = "location")
    private String location;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    private Boolean isActive;
}
