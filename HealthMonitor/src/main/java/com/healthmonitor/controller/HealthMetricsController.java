package com.healthmonitor.controller;

import com.healthmonitor.dto.HealthMetricsDTO;
import com.healthmonitor.model.HealthMetrics;
import com.healthmonitor.model.User;
import com.healthmonitor.service.HealthMetricsService;
import com.healthmonitor.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Health Metrics management
 */
@RestController
@RequestMapping("/api/users/{userId}/metrics")
@CrossOrigin(origins = "*")
public class HealthMetricsController {
    
    private final HealthMetricsService healthMetricsService;
    private final UserService userService;
    
    @Autowired
    public HealthMetricsController(HealthMetricsService healthMetricsService,
                                   UserService userService) {
        this.healthMetricsService = healthMetricsService;
        this.userService = userService;
    }
    
    /**
     * Create or update health metrics for a user on a specific date
     */
    @PostMapping
    public ResponseEntity<HealthMetricsDTO> saveMetrics(@PathVariable Long userId,
                                                        @Valid @RequestBody HealthMetricsDTO metricsDTO) {
        User user = userService.getUserById(userId)
            .orElse(null);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        HealthMetrics metrics = healthMetricsService.saveOrUpdateMetrics(
            user, 
            metricsDTO.getDate() != null ? metricsDTO.getDate() : LocalDate.now(),
            metricsDTO.toEntity()
        );
        
        return new ResponseEntity<>(new HealthMetricsDTO(metrics), HttpStatus.OK);
    }
    
    /**
     * Get health metrics for a user on a specific date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<HealthMetricsDTO> getMetricsByDate(
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        User user = userService.getUserById(userId)
            .orElse(null);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return healthMetricsService.getMetricsByUserAndDate(user, date)
            .map(metrics -> new ResponseEntity<>(new HealthMetricsDTO(metrics), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Get health metrics for a user within a date range
     */
    @GetMapping("/range")
    public ResponseEntity<List<HealthMetricsDTO>> getMetricsByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        User user = userService.getUserById(userId)
            .orElse(null);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        List<HealthMetricsDTO> metrics = healthMetricsService
            .getMetricsByUserAndDateRange(user, startDate, endDate)
            .stream()
            .map(HealthMetricsDTO::new)
            .collect(Collectors.toList());
        
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }
    
    /**
     * Add steps to user's daily metrics
     */
    @PostMapping("/steps")
    public ResponseEntity<HealthMetricsDTO> addSteps(
            @PathVariable Long userId,
            @RequestParam int steps,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        User user = userService.getUserById(userId)
            .orElse(null);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        LocalDate targetDate = date != null ? date : LocalDate.now();
        HealthMetrics metrics = healthMetricsService.addSteps(user, targetDate, steps);
        
        return new ResponseEntity<>(new HealthMetricsDTO(metrics), HttpStatus.OK);
    }
    
    /**
     * Get today's metrics for a user
     */
    @GetMapping("/today")
    public ResponseEntity<HealthMetricsDTO> getTodayMetrics(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElse(null);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return healthMetricsService.getMetricsByUserAndDate(user, LocalDate.now())
            .map(metrics -> new ResponseEntity<>(new HealthMetricsDTO(metrics), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}

