package com.healthmonitor.service.calculator.impl;

import com.healthmonitor.model.User;
import com.healthmonitor.service.calculator.BMRAnalyzer;
import org.springframework.stereotype.Service;

/**
 * Implementation of BMRAnalyzer using Mifflin-St Jeor Equation
 * This is the most accurate BMR formula currently available
 * Demonstrates polymorphism - implements BMRAnalyzer interface
 */
@Service
public class MifflinStJeorBMRAnalyzer implements BMRAnalyzer {
    
    private static final double MALE_CONSTANT = 5.0;
    private static final double FEMALE_CONSTANT = -161.0;
    private static final double WEIGHT_MULTIPLIER = 10.0;
    private static final double HEIGHT_MULTIPLIER = 6.25;
    private static final double AGE_MULTIPLIER = 5.0;
    
    @Override
    public double calculateBMR(User user) {
        if (user == null || user.getWeightKg() == null || 
            user.getHeightCm() == null || user.getDateOfBirth() == null) {
            throw new IllegalArgumentException("User data incomplete for BMR calculation");
        }
        
        double weight = user.getWeightKg();
        double height = user.getHeightCm();
        int age = user.calculateAge();
        double constant = user.getGender() == User.Gender.MALE ? MALE_CONSTANT : FEMALE_CONSTANT;
        
        // Mifflin-St Jeor Equation:
        // BMR = (10 × weight) + (6.25 × height) - (5 × age) + constant
        return (WEIGHT_MULTIPLIER * weight) + 
               (HEIGHT_MULTIPLIER * height) - 
               (AGE_MULTIPLIER * age) + 
               constant;
    }
    
    @Override
    public double calculateTDEE(User user) {
        double bmr = calculateBMR(user);
        
        if (user.getActivityLevel() == null) {
            return bmr * 1.2; // Default to sedentary
        }
        
        return bmr * user.getActivityLevel().getMultiplier();
    }
}

