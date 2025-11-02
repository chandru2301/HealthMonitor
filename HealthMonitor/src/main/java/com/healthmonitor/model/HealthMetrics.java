package com.healthmonitor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * HealthMetrics entity
 * Tracks daily health metrics for users
 */
@Entity
@Table(name = "health_metrics", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}))
public class HealthMetrics extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull(message = "Date is required")
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Min(value = 0, message = "Steps cannot be negative")
    @Column(name = "steps")
    private Integer steps;
    
    @DecimalMin(value = "0.0", message = "Calories consumed cannot be negative")
    @Column(name = "calories_consumed")
    private Double caloriesConsumed;
    
    @DecimalMin(value = "0.0", message = "Calories burned cannot be negative")
    @Column(name = "calories_burned")
    private Double caloriesBurned;
    
    @DecimalMin(value = "0.0", message = "Distance cannot be negative")
    @Column(name = "distance_km")
    private Double distanceKm;
    
    @Min(value = 0, message = "Active minutes cannot be negative")
    @Column(name = "active_minutes")
    private Integer activeMinutes;
    
    @Column(name = "water_intake_liters")
    @DecimalMin(value = "0.0", message = "Water intake cannot be negative")
    private Double waterIntakeLiters;
    
    @Column(name = "sleep_hours")
    @DecimalMin(value = "0.0", message = "Sleep hours cannot be negative")
    private Double sleepHours;
    
    @Column(name = "heart_rate_avg")
    @Min(value = 0, message = "Heart rate cannot be negative")
    private Integer heartRateAvg;
    
    // Constructors
    public HealthMetrics() {
    }
    
    public HealthMetrics(User user, LocalDate date) {
        this.user = user;
        this.date = date;
        this.steps = 0;
        this.caloriesConsumed = 0.0;
        this.caloriesBurned = 0.0;
        this.distanceKm = 0.0;
        this.activeMinutes = 0;
        this.waterIntakeLiters = 0.0;
        this.sleepHours = 0.0;
        this.heartRateAvg = 0;
    }
    
    // Business methods
    public double calculateNetCalories() {
        return (caloriesConsumed != null ? caloriesConsumed : 0.0) - 
               (caloriesBurned != null ? caloriesBurned : 0.0);
    }
    
    public boolean isDailyGoalMet(int dailyStepGoal) {
        return steps != null && steps >= dailyStepGoal;
    }
    
    // Getters and Setters
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
}

