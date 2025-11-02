package com.healthmonitor.service;

import com.healthmonitor.model.User;
import com.healthmonitor.repository.UserRepository;
import com.healthmonitor.service.calculator.BMRAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing users
 * Demonstrates service layer pattern and business logic encapsulation
 */
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final BMRAnalyzer bmrAnalyzer;
    
    @Autowired
    public UserService(UserRepository userRepository,
                      BMRAnalyzer bmrAnalyzer) {
        this.userRepository = userRepository;
        this.bmrAnalyzer = bmrAnalyzer;
    }
    
    /**
     * Create a new user
     */
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }
    
    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Update user
     */
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        // Check email uniqueness if changed
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) && 
            userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new IllegalArgumentException("User with email " + updatedUser.getEmail() + " already exists");
        }
        
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setHeightCm(updatedUser.getHeightCm());
        existingUser.setWeightKg(updatedUser.getWeightKg());
        existingUser.setActivityLevel(updatedUser.getActivityLevel());
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Delete user
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    /**
     * Calculate BMR for a user
     */
    @Transactional(readOnly = true)
    public double calculateBMR(Long userId) {
        User user = getUserById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return bmrAnalyzer.calculateBMR(user);
    }
    
    /**
     * Calculate TDEE for a user
     */
    @Transactional(readOnly = true)
    public double calculateTDEE(Long userId) {
        User user = getUserById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return bmrAnalyzer.calculateTDEE(user);
    }
}

