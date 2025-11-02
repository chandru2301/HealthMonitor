package com.healthmonitor.service;

import com.healthmonitor.model.Activity;
import com.healthmonitor.model.User;
import com.healthmonitor.repository.ActivityRepository;
import com.healthmonitor.service.calculator.CalorieCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing activities
 * Demonstrates service layer pattern and business logic encapsulation
 */
@Service
@Transactional
public class ActivityService {
    
    private final ActivityRepository activityRepository;
    private final CalorieCalculator calorieCalculator;
    private final HealthMetricsService healthMetricsService;
    
    @Autowired
    public ActivityService(ActivityRepository activityRepository,
                          CalorieCalculator calorieCalculator,
                          HealthMetricsService healthMetricsService) {
        this.activityRepository = activityRepository;
        this.calorieCalculator = calorieCalculator;
        this.healthMetricsService = healthMetricsService;
    }
    
    /**
     * Create a new activity and automatically calculate calories burned
     */
    public Activity createActivity(User user, String activityType, 
                                   LocalDateTime startTime, LocalDateTime endTime,
                                   Double distanceKm) {
        Activity activity = new Activity(user, activityType, startTime, endTime);
        
        if (distanceKm != null) {
            activity.setDistanceKm(distanceKm);
        }
        
        // Calculate calories burned automatically
        double caloriesBurned = calorieCalculator.calculateCaloriesBurned(
            activityType, 
            activity.getDurationMinutes(), 
            user.getWeightKg()
        );
        activity.setCaloriesBurned(caloriesBurned);
        
        // Save activity
        Activity savedActivity = activityRepository.save(activity);
        
        // Update health metrics for the activity date
        LocalDate activityDate = startTime.toLocalDate();
        updateHealthMetricsFromActivity(user, activityDate, savedActivity);
        
        return savedActivity;
    }
    
    /**
     * Get activity by ID
     */
    @Transactional(readOnly = true)
    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }
    
    /**
     * Get all activities for a user
     */
    @Transactional(readOnly = true)
    public List<Activity> getActivitiesByUser(User user) {
        return activityRepository.findByUserOrderByStartTimeDesc(user);
    }
    
    /**
     * Get activities for a user within a date range
     */
    @Transactional(readOnly = true)
    public List<Activity> getActivitiesByUserAndDateRange(User user, 
                                                           LocalDate startDate, 
                                                           LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        return activityRepository.findByUserAndStartTimeBetween(user, startDateTime, endDateTime);
    }
    
    /**
     * Update activity
     */
    public Activity updateActivity(Long id, Activity updatedActivity) {
        Activity existingActivity = activityRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Activity not found with id: " + id));
        
        existingActivity.setActivityType(updatedActivity.getActivityType());
        existingActivity.setStartTime(updatedActivity.getStartTime());
        existingActivity.setEndTime(updatedActivity.getEndTime());
        
        if (updatedActivity.getDistanceKm() != null) {
            existingActivity.setDistanceKm(updatedActivity.getDistanceKm());
        }
        
        // Recalculate calories
        double caloriesBurned = calorieCalculator.calculateCaloriesBurned(
            existingActivity.getActivityType(),
            existingActivity.getDurationMinutes(),
            existingActivity.getUser().getWeightKg()
        );
        existingActivity.setCaloriesBurned(caloriesBurned);
        
        if (updatedActivity.getNotes() != null) {
            existingActivity.setNotes(updatedActivity.getNotes());
        }
        
        return activityRepository.save(existingActivity);
    }
    
    /**
     * Delete activity
     */
    public void deleteActivity(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new IllegalArgumentException("Activity not found with id: " + id);
        }
        activityRepository.deleteById(id);
    }
    
    /**
     * Update health metrics based on activity
     */
    private void updateHealthMetricsFromActivity(User user, LocalDate date, Activity activity) {
        Optional<com.healthmonitor.model.HealthMetrics> metricsOpt = 
            healthMetricsService.getMetricsByUserAndDate(user, date);
        
        com.healthmonitor.model.HealthMetrics metrics;
        if (metricsOpt.isPresent()) {
            metrics = metricsOpt.get();
            // Add to existing values
            metrics.setCaloriesBurned(
                (metrics.getCaloriesBurned() != null ? metrics.getCaloriesBurned() : 0.0) +
                (activity.getCaloriesBurned() != null ? activity.getCaloriesBurned() : 0.0)
            );
            metrics.setActiveMinutes(
                (metrics.getActiveMinutes() != null ? metrics.getActiveMinutes() : 0) +
                activity.getDurationMinutes().intValue()
            );
            if (activity.getDistanceKm() != null) {
                metrics.setDistanceKm(
                    (metrics.getDistanceKm() != null ? metrics.getDistanceKm() : 0.0) +
                    activity.getDistanceKm()
                );
            }
        } else {
            metrics = new com.healthmonitor.model.HealthMetrics(user, date);
            metrics.setCaloriesBurned(activity.getCaloriesBurned() != null ? 
                                     activity.getCaloriesBurned() : 0.0);
            metrics.setActiveMinutes(activity.getDurationMinutes().intValue());
            metrics.setDistanceKm(activity.getDistanceKm() != null ? 
                                 activity.getDistanceKm() : 0.0);
        }
        
        healthMetricsService.saveOrUpdateMetrics(user, date, metrics);
    }
}

