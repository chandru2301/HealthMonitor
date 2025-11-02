package com.healthmonitor.service.calculator.impl;

import com.healthmonitor.service.calculator.CalorieCalculator;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Standard Calorie Calculator implementation
 * Uses MET (Metabolic Equivalent) values to calculate calories burned
 * Demonstrates polymorphism - implements CalorieCalculator interface
 */
@Service
public class StandardCalorieCalculator implements CalorieCalculator {
    
    // MET values database for common activities
    // MET = metabolic equivalent of task
    private static final Map<String, Double> MET_VALUES = new HashMap<>();
    
    static {
        // Light intensity activities (MET: 2.0 - 3.0)
        MET_VALUES.put("WALKING_SLOW", 2.5);
        MET_VALUES.put("WALKING", 3.5);
        MET_VALUES.put("CYCLING_LIGHT", 4.0);
        
        // Moderate intensity activities (MET: 3.0 - 6.0)
        MET_VALUES.put("WALKING_FAST", 5.0);
        MET_VALUES.put("JOGGING", 7.0);
        MET_VALUES.put("RUNNING", 9.8);
        MET_VALUES.put("CYCLING_MODERATE", 6.8);
        MET_VALUES.put("SWIMMING", 6.0);
        MET_VALUES.put("YOGA", 3.0);
        
        // Vigorous intensity activities (MET: 6.0+)
        MET_VALUES.put("RUNNING_FAST", 11.5);
        MET_VALUES.put("CYCLING_FAST", 10.0);
        MET_VALUES.put("SWIMMING_VIGOROUS", 10.0);
        MET_VALUES.put("JUMPING_ROPE", 12.0);
        MET_VALUES.put("BASKETBALL", 8.0);
        MET_VALUES.put("TENNIS", 7.0);
        MET_VALUES.put("SOCCER", 7.0);
        
        // Default MET value for unknown activities
        MET_VALUES.put("DEFAULT", 3.5);
    }
    
    /**
     * Calculates calories burned using MET formula:
     * Calories = MET × weight(kg) × time(hours)
     * 
     * @param activityType Type of activity
     * @param durationMinutes Duration in minutes
     * @param weightKg Weight in kilograms
     * @return Calories burned
     */
    @Override
    public double calculateCaloriesBurned(String activityType, double durationMinutes, double weightKg) {
        if (durationMinutes <= 0 || weightKg <= 0) {
            return 0.0;
        }
        
        double met = getMETValue(activityType);
        double durationHours = durationMinutes / 60.0;
        
        // Formula: Calories = MET × weight(kg) × time(hours)
        return met * weightKg * durationHours;
    }
    
    @Override
    public double getMETValue(String activityType) {
        if (activityType == null || activityType.trim().isEmpty()) {
            return MET_VALUES.get("DEFAULT");
        }
        
        String normalizedActivity = activityType.toUpperCase().trim();
        
        // Try exact match first
        if (MET_VALUES.containsKey(normalizedActivity)) {
            return MET_VALUES.get(normalizedActivity);
        }
        
        // Try partial match for common variations
        for (Map.Entry<String, Double> entry : MET_VALUES.entrySet()) {
            if (normalizedActivity.contains(entry.getKey()) || 
                entry.getKey().contains(normalizedActivity)) {
                return entry.getValue();
            }
        }
        
        // Default MET value if activity not found
        return MET_VALUES.get("DEFAULT");
    }
}

