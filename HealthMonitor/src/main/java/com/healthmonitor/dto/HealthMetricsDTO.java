package com.healthmonitor.dto;

import com.healthmonitor.model.HealthMetrics;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Data Transfer Object for HealthMetrics
 */
public class HealthMetricsDTO {
    
    private Long id;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @Min(value = 0, message = "Steps cannot be negative")
    private Integer steps;
    
    @DecimalMin(value = "0.0", message = "Calories consumed cannot be negative")
    private Double caloriesConsumed;
    
    @DecimalMin(value = "0.0", message = "Calories burned cannot be negative")
    private Double caloriesBurned;
    
    @DecimalMin(value = "0.0", message = "Distance cannot be negative")
    private Double distanceKm;
    
    @Min(value = 0, message = "Active minutes cannot be negative")
    private Integer activeMinutes;
    
    @DecimalMin(value = "0.0", message = "Water intake cannot be negative")
    private Double waterIntakeLiters;
    
    @DecimalMin(value = "0.0", message = "Sleep hours cannot be negative")
    private Double sleepHours;
    
    @Min(value = 0, message = "Heart rate cannot be negative")
    private Integer heartRateAvg;
    
    private Double netCalories;
    
    // Constructors
    public HealthMetricsDTO() {
    }
    
    public HealthMetricsDTO(HealthMetrics metrics) {
        this.id = metrics.getId();
        this.date = metrics.getDate();
        this.steps = metrics.getSteps();
        this.caloriesConsumed = metrics.getCaloriesConsumed();
        this.caloriesBurned = metrics.getCaloriesBurned();
        this.distanceKm = metrics.getDistanceKm();
        this.activeMinutes = metrics.getActiveMinutes();
        this.waterIntakeLiters = metrics.getWaterIntakeLiters();
        this.sleepHours = metrics.getSleepHours();
        this.heartRateAvg = metrics.getHeartRateAvg();
        this.netCalories = metrics.calculateNetCalories();
    }
    
    // Convert to Entity
    public HealthMetrics toEntity() {
        HealthMetrics metrics = new HealthMetrics();
        metrics.setId(this.id);
        metrics.setDate(this.date);
        metrics.setSteps(this.steps);
        metrics.setCaloriesConsumed(this.caloriesConsumed);
        metrics.setCaloriesBurned(this.caloriesBurned);
        metrics.setDistanceKm(this.distanceKm);
        metrics.setActiveMinutes(this.activeMinutes);
        metrics.setWaterIntakeLiters(this.waterIntakeLiters);
        metrics.setSleepHours(this.sleepHours);
        metrics.setHeartRateAvg(this.heartRateAvg);
        return metrics;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Integer getSteps() {
        return steps;
    }
    
    public void setSteps(Integer steps) {
        this.steps = steps;
    }
    
    public Double getCaloriesConsumed() {
        return caloriesConsumed;
    }
    
    public void setCaloriesConsumed(Double caloriesConsumed) {
        this.caloriesConsumed = caloriesConsumed;
    }
    
    public Double getCaloriesBurned() {
        return caloriesBurned;
    }
    
    public void setCaloriesBurned(Double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
    
    public Double getDistanceKm() {
        return distanceKm;
    }
    
    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
    
    public Integer getActiveMinutes() {
        return activeMinutes;
    }
    
    public void setActiveMinutes(Integer activeMinutes) {
        this.activeMinutes = activeMinutes;
    }
    
    public Double getWaterIntakeLiters() {
        return waterIntakeLiters;
    }
    
    public void setWaterIntakeLiters(Double waterIntakeLiters) {
        this.waterIntakeLiters = waterIntakeLiters;
    }
    
    public Double getSleepHours() {
        return sleepHours;
    }
    
    public void setSleepHours(Double sleepHours) {
        this.sleepHours = sleepHours;
    }
    
    public Integer getHeartRateAvg() {
        return heartRateAvg;
    }
    
    public void setHeartRateAvg(Integer heartRateAvg) {
        this.heartRateAvg = heartRateAvg;
    }
    
    public Double getNetCalories() {
        return netCalories;
    }
    
    public void setNetCalories(Double netCalories) {
        this.netCalories = netCalories;
    }
}

