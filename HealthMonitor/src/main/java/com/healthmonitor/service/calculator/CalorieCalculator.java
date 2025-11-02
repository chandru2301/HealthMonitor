package com.healthmonitor.service.calculator;

/**
 * Calorie Calculator interface demonstrating abstraction
 * Different implementations can calculate calories burned for different activities
 */
public interface CalorieCalculator {
    
    /**
     * Calculates calories burned based on activity type, duration, and user weight
     * 
     * @param activityType Type of activity (running, walking, cycling, etc.)
     * @param durationMinutes Duration of activity in minutes
     * @param weightKg User's weight in kilograms
     * @return Calories burned
     */
    double calculateCaloriesBurned(String activityType, double durationMinutes, double weightKg);
    
    /**
     * Gets the MET (Metabolic Equivalent) value for an activity
     * MET values represent the energy cost of physical activities
     * 
     * @param activityType Type of activity
     * @return MET value
     */
    double getMETValue(String activityType);
}

