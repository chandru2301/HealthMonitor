package com.healthmonitor.repository;

import com.healthmonitor.model.Activity;
import com.healthmonitor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Activity entity
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    /**
     * Find all activities for a user, ordered by start time descending
     */
    List<Activity> findByUserOrderByStartTimeDesc(User user);
    
    /**
     * Find activities by user and date range
     */
    List<Activity> findByUserAndStartTimeBetween(User user, LocalDateTime start, LocalDateTime end);
    
    /**
     * Find activities by user and activity type
     */
    List<Activity> findByUserAndActivityType(User user, String activityType);
}

