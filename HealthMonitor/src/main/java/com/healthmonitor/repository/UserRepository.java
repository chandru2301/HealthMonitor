package com.healthmonitor.repository;

import com.healthmonitor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity
 * Demonstrates Spring Data JPA repository pattern
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     * @param email Email address
     * @return Optional User
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email
     * @param email Email address
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
}

