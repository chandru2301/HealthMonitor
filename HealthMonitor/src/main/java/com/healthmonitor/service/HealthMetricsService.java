package com.healthmonitor.service;

import com.healthmonitor.model.HealthMetrics;
import com.healthmonitor.model.User;
import com.healthmonitor.repository.HealthMetricsRepository;
import com.healthmonitor.service.calculator.BMRAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing health metrics
 * Demonstrates service layer pattern and encapsulation
 */
@Service
@Transactional
public class HealthMetricsService {
    
    private final HealthMetricsRepository healthMetricsRepository;
    private final BMRAnalyzer bmrAnalyzer;
    
    @Autowired
    public HealthMetricsService(HealthMetricsRepository healthMetricsRepository,
                                 BMRAnalyzer bmrAnalyzer) {
        this.healthMetricsRepository = healthMetricsRepository;
        this.bmrAnalyzer = bmrAnalyzer;
    }
    
    /**
     * Create or update health metrics for a user on a specific date
     */
    public HealthMetrics saveOrUpdateMetrics(User user, LocalDate date, HealthMetrics metrics) {
        Optional<HealthMetrics> existingMetrics = 
            healthMetricsRepository.findByUserAndDate(user, date);
        
        HealthMetrics healthMetrics;
        if (existingMetrics.isPresent()) {
            healthMetrics = existingMetrics.get();
            updateMetrics(healthMetrics, metrics);
        } else {
            healthMetrics = new HealthMetrics(user, date);
            updateMetrics(healthMetrics, metrics);
        }
        
        return healthMetricsRepository.save(healthMetrics);
    }
    
    /**
     * Get health metrics for a user on a specific date
     */
    @Transactional(readOnly = true)
    public Optional<HealthMetrics> getMetricsByUserAndDate(User user, LocalDate date) {
        return healthMetricsRepository.findByUserAndDate(user, date);
    }
    
    /**
     * Get all health metrics for a user within a date range
     */
    @Transactional(readOnly = true)
    public List<HealthMetrics> getMetricsByUserAndDateRange(User user, 
                                                             LocalDate startDate, 
                                                             LocalDate endDate) {
        return healthMetricsRepository.findByUserAndDateBetween(user, startDate, endDate);
    }
    
    /**
     * Add steps to existing metrics or create new entry
     */
    public HealthMetrics addSteps(User user, LocalDate date, int steps) {
        Optional<HealthMetrics> existingMetrics = 
            healthMetricsRepository.findByUserAndDate(user, date);
        
        HealthMetrics healthMetrics;
        if (existingMetrics.isPresent()) {
            healthMetrics = existingMetrics.get();
            healthMetrics.setSteps((healthMetrics.getSteps() != null ? 
                                    healthMetrics.getSteps() : 0) + steps);
        } else {
            healthMetrics = new HealthMetrics(user, date);
            healthMetrics.setSteps(steps);
        }
        
        // Update distance based on steps (average: 1 km = 1300 steps)
        if (healthMetrics.getDistanceKm() == null) {
            healthMetrics.setDistanceKm(0.0);
        }
        healthMetrics.setDistanceKm(healthMetrics.getDistanceKm() + (steps / 1300.0));
        
        return healthMetricsRepository.save(healthMetrics);
    }
    
    /**
     * Calculate and update BMR and TDEE for user
     */
    public void updateBMRMetrics(User user, LocalDate date) {
        Optional<HealthMetrics> metricsOpt = 
            healthMetricsRepository.findByUserAndDate(user, date);
        
        if (metricsOpt.isPresent()) {
            HealthMetrics metrics = metricsOpt.get();
            double bmr = bmrAnalyzer.calculateBMR(user);
            double tdee = bmrAnalyzer.calculateTDEE(user);
            // Store in notes or extend model if needed
            // For now, we'll assume these are calculated on-demand
        }
    }
    
    private void updateMetrics(HealthMetrics existing, HealthMetrics newMetrics) {
        if (newMetrics.getSteps() != null) {
            existing.setSteps(newMetrics.getSteps());
        }
        if (newMetrics.getCaloriesConsumed() != null) {
            existing.setCaloriesConsumed(newMetrics.getCaloriesConsumed());
        }
        if (newMetrics.getCaloriesBurned() != null) {
            existing.setCaloriesBurned(newMetrics.getCaloriesBurned());
        }
        if (newMetrics.getDistanceKm() != null) {
            existing.setDistanceKm(newMetrics.getDistanceKm());
        }
        if (newMetrics.getActiveMinutes() != null) {
            existing.setActiveMinutes(newMetrics.getActiveMinutes());
        }
        if (newMetrics.getWaterIntakeLiters() != null) {
            existing.setWaterIntakeLiters(newMetrics.getWaterIntakeLiters());
        }
        if (newMetrics.getSleepHours() != null) {
            existing.setSleepHours(newMetrics.getSleepHours());
        }
        if (newMetrics.getHeartRateAvg() != null) {
            existing.setHeartRateAvg(newMetrics.getHeartRateAvg());
        }
    }
}

