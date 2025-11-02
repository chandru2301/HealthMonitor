package com.healthmonitor.controller;

import com.healthmonitor.model.User;
import com.healthmonitor.service.HealthMetricsService;
import com.healthmonitor.service.UserService;
import com.healthmonitor.service.calculator.BMRAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for health monitoring dashboard and analytics
 * Demonstrates aggregation and analysis operations
 */
@RestController
@RequestMapping("/api/users/{userId}/dashboard")
public class HealthMonitorController {
    
    private final UserService userService;
    private final HealthMetricsService healthMetricsService;
    private final BMRAnalyzer bmrAnalyzer;
    
    @Autowired
    public HealthMonitorController(UserService userService,
                                   HealthMetricsService healthMetricsService,
                                   BMRAnalyzer bmrAnalyzer) {
        this.userService = userService;
        this.healthMetricsService = healthMetricsService;
        this.bmrAnalyzer = bmrAnalyzer;
    }
    
    /**
     * Get health summary for a user
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getHealthSummary(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElse(null);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("userId", user.getId());
        summary.put("name", user.getName());
        summary.put("age", user.calculateAge());
        summary.put("bmi", user.calculateBMI());
        summary.put("bmr", bmrAnalyzer.calculateBMR(user));
        summary.put("tdee", bmrAnalyzer.calculateTDEE(user));
        
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }
    
    /**
     * Get weekly statistics
     */
    @GetMapping("/weekly")
    public ResponseEntity<Map<String, Object>> getWeeklyStats(
            @PathVariable Long userId,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate) {
        
        User user = userService.getUserById(userId)
            .orElse(null);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        LocalDate startDate = weekStartDate != null ? 
            weekStartDate : LocalDate.now().minusDays(7);
        LocalDate endDate = startDate.plusDays(7);
        
        var metrics = healthMetricsService.getMetricsByUserAndDateRange(user, startDate, endDate);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("startDate", startDate);
        stats.put("endDate", endDate);
        
        int totalSteps = metrics.stream()
            .mapToInt(m -> m.getSteps() != null ? m.getSteps() : 0)
            .sum();
        
        double totalCaloriesBurned = metrics.stream()
            .mapToDouble(m -> m.getCaloriesBurned() != null ? m.getCaloriesBurned() : 0.0)
            .sum();
        
        double totalCaloriesConsumed = metrics.stream()
            .mapToDouble(m -> m.getCaloriesConsumed() != null ? m.getCaloriesConsumed() : 0.0)
            .sum();
        
        double totalDistance = metrics.stream()
            .mapToDouble(m -> m.getDistanceKm() != null ? m.getDistanceKm() : 0.0)
            .sum();
        
        int totalActiveMinutes = metrics.stream()
            .mapToInt(m -> m.getActiveMinutes() != null ? m.getActiveMinutes() : 0)
            .sum();
        
        stats.put("totalSteps", totalSteps);
        stats.put("totalCaloriesBurned", totalCaloriesBurned);
        stats.put("totalCaloriesConsumed", totalCaloriesConsumed);
        stats.put("netCalories", totalCaloriesConsumed - totalCaloriesBurned);
        stats.put("totalDistanceKm", totalDistance);
        stats.put("totalActiveMinutes", totalActiveMinutes);
        stats.put("averageStepsPerDay", totalSteps / 7.0);
        stats.put("averageActiveMinutesPerDay", totalActiveMinutes / 7.0);
        
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
}

