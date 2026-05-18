package com.covoiturage.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class TrajetForm {
    @NotBlank
    private String depart;

    @NotBlank
    private String arrivee;

    @Min(1)
    private Integer dureeMinutes = 30;

    @PositiveOrZero
    private Double prix = 0.0;

    @Min(1)
    private Integer maxPlaces = 1;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDateTime;

    private boolean recurring = false;

    private WeeklyScheduleDto weekly = new WeeklyScheduleDto();

    private String weeklySchedule = "";
}
