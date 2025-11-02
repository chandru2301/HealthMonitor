package com.healthmonitor.dto;

import com.healthmonitor.model.Activity;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Activity
 */
public class ActivityDTO {
    
    private Long id;
    
    @NotBlank(message = "Activity type is required")
    private String activityType;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
    
    @DecimalMin(value = "0.0", message = "Duration cannot be negative")
    private Double durationMinutes;
    
    @DecimalMin(value = "0.0", message = "Calories burned cannot be negative")
    private Double caloriesBurned;
    
    @DecimalMin(value = "0.0", message = "Distance cannot be negative")
    private Double distanceKm;
    
    private String notes;
    
    private Double averagePace; // km per hour
    
    // Constructors
    public ActivityDTO() {
    }
    
    public ActivityDTO(Activity activity) {
        this.id = activity.getId();
        this.activityType = activity.getActivityType();
        this.startTime = activity.getStartTime();
        this.endTime = activity.getEndTime();
        this.durationMinutes = activity.getDurationMinutes();
        this.caloriesBurned = activity.getCaloriesBurned();
        this.distanceKm = activity.getDistanceKm();
        this.notes = activity.getNotes();
        this.averagePace = activity.getAveragePace();
    }
    
    // Convert to Entity (without user - will be set in service)
    public Activity toEntity() {
        Activity activity = new Activity();
        activity.setId(this.id);
        activity.setActivityType(this.activityType);
        activity.setStartTime(this.startTime);
        activity.setEndTime(this.endTime);
        activity.setDistanceKm(this.distanceKm);
        activity.setNotes(this.notes);
        return activity;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
    
    public Double getAveragePace() {
        return averagePace;
    }
    
    public void setAveragePace(Double averagePace) {
        this.averagePace = averagePace;
    }
}

