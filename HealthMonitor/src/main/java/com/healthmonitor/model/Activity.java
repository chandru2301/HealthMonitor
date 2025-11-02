package com.healthmonitor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Activity entity
 * Represents individual activities/exercises performed by users
 */
@Entity
@Table(name = "activities")
public class Activity extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank(message = "Activity type is required")
    @Column(name = "activity_type", nullable = false)
    private String activityType;
    
    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @DecimalMin(value = "0.0", message = "Duration cannot be negative")
    @Column(name = "duration_minutes", nullable = false)
    private Double durationMinutes;
    
    @DecimalMin(value = "0.0", message = "Calories burned cannot be negative")
    @Column(name = "calories_burned")
    private Double caloriesBurned;
    
    @DecimalMin(value = "0.0", message = "Distance cannot be negative")
    @Column(name = "distance_km")
    private Double distanceKm;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    // Constructors
    public Activity() {
    }
    
    public Activity(User user, String activityType, LocalDateTime startTime, 
                   LocalDateTime endTime) {
        this.user = user;
        this.activityType = activityType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = calculateDurationMinutes(startTime, endTime);
    }
    
    // Business methods
    private double calculateDurationMinutes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || end.isBefore(start)) {
            return 0.0;
        }
        return java.time.Duration.between(start, end).toMinutes();
    }
    
    public double getAveragePace() {
        if (durationMinutes == null || durationMinutes == 0 || distanceKm == null || distanceKm == 0) {
            return 0.0;
        }
        return distanceKm / (durationMinutes / 60.0); // km per hour
    }
    
    // Getters and Setters
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getActivityType() {
        return activityType;
    }
    
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        if (this.endTime != null) {
            this.durationMinutes = calculateDurationMinutes(startTime, this.endTime);
        }
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        if (this.startTime != null) {
            this.durationMinutes = calculateDurationMinutes(this.startTime, endTime);
        }
    }
    
    public Double getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Double durationMinutes) {
        this.durationMinutes = durationMinutes;
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
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}

