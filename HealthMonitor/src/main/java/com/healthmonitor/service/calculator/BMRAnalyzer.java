package com.healthmonitor.service.calculator;

import com.healthmonitor.model.User;

/**
 * BMR Analyzer interface demonstrating abstraction
 * Different implementations can use different formulas (Mifflin-St Jeor, Harris-Benedict, etc.)
 */
public interface BMRAnalyzer {
    
    /**
     * Calculates Basal Metabolic Rate (BMR)
     * BMR is the number of calories the body burns at rest
     * 
     * @param user The user for whom to calculate BMR
     * @return BMR in calories per day
     */
    double calculateBMR(User user);
    
    /**
     * Calculates Total Daily Energy Expenditure (TDEE)
     * TDEE = BMR * Activity Level Multiplier
     * 
     * @param user The user for whom to calculate TDEE
     * @return TDEE in calories per day
     */
    double calculateTDEE(User user);
}

