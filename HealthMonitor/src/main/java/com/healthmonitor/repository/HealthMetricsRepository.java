package com.healthmonitor.repository;

import com.healthmonitor.model.HealthMetrics;
import com.healthmonitor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for HealthMetrics entity
 */
@Repository
public interface HealthMetricsRepository extends JpaRepository<HealthMetrics, Long> {
    
    /**
     * Find health metrics by user and date
     */
    Optional<HealthMetrics> findByUserAndDate(User user, LocalDate date);
    
    /**
     * Find health metrics by user and date range
     */
    List<HealthMetrics> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all health metrics for a user, ordered by date descending
     */
    List<HealthMetrics> findByUserOrderByDateDesc(User user);
}

