package com.healthmonitor.controller;

import com.healthmonitor.dto.ActivityDTO;
import com.healthmonitor.model.Activity;
import com.healthmonitor.model.User;
import com.healthmonitor.service.ActivityService;
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
 * REST Controller for Activity management
 */
@RestController
@RequestMapping("/api/users/{userId}/activities")
@CrossOrigin(origins = "*")
public class ActivityController {
    
    private final ActivityService activityService;
    private final UserService userService;
    
    @Autowired
    public ActivityController(ActivityService activityService, UserService userService) {
        this.activityService = activityService;
        this.userService = userService;
    }
    
    /**
     * Create a new activity
     */
    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(@PathVariable Long userId,
                                                       @Valid @RequestBody ActivityDTO activityDTO) {
        User user = userService.getUserById(userId)
            .orElse(null);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Activity activity = activityService.createActivity(
            user,
            activityDTO.getActivityType(),
            activityDTO.getStartTime(),
            activityDTO.getEndTime(),
            activityDTO.getDistanceKm()
        );
        
        return new ResponseEntity<>(new ActivityDTO(activity), HttpStatus.CREATED);
    }
    
    /**
     * Get activity by ID
     */
    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long userId,
                                                       @PathVariable Long activityId) {
        return activityService.getActivityById(activityId)
            .filter(activity -> activity.getUser().getId().equals(userId))
            .map(activity -> new ResponseEntity<>(new ActivityDTO(activity), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Get all activities for a user
     */
    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getAllActivities(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElse(null);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        List<ActivityDTO> activities = activityService.getActivitiesByUser(user)
            .stream()
            .map(ActivityDTO::new)
            .collect(Collectors.toList());
        
        return new ResponseEntity<>(activities, HttpStatus.OK);
    }
    
    /**
     * Get activities for a user within a date range
     */
    @GetMapping("/range")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        User user = userService.getUserById(userId)
            .orElse(null);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        List<ActivityDTO> activities = activityService
            .getActivitiesByUserAndDateRange(user, startDate, endDate)
            .stream()
            .map(ActivityDTO::new)
            .collect(Collectors.toList());
        
        return new ResponseEntity<>(activities, HttpStatus.OK);
    }
    
    /**
     * Update activity
     */
    @PutMapping("/{activityId}")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable Long userId,
                                                       @PathVariable Long activityId,
                                                       @Valid @RequestBody ActivityDTO activityDTO) {
        try {
            Activity updatedActivity = activityService.updateActivity(activityId, activityDTO.toEntity());
            
            if (!updatedActivity.getUser().getId().equals(userId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            return new ResponseEntity<>(new ActivityDTO(updatedActivity), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Delete activity
     */
    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long userId,
                                                @PathVariable Long activityId) {
        try {
            activityService.deleteActivity(activityId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

