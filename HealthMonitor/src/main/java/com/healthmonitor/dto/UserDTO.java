package com.healthmonitor.dto;

import com.healthmonitor.model.User;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Data Transfer Object for User
 * Demonstrates DTO pattern for API communication
 */
public class UserDTO {
    
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @NotNull(message = "Gender is required")
    private User.Gender gender;
    
    @NotNull(message = "Height is required")
    @DecimalMin(value = "50.0")
    @DecimalMax(value = "300.0")
    private Double heightCm;
    
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "20.0")
    @DecimalMax(value = "500.0")
    private Double weightKg;
    
    private User.ActivityLevel activityLevel;
    
    // Constructors
    public UserDTO() {
    }
    
    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.dateOfBirth = user.getDateOfBirth();
        this.gender = user.getGender();
        this.heightCm = user.getHeightCm();
        this.weightKg = user.getWeightKg();
        this.activityLevel = user.getActivityLevel();
    }
    
    // Convert to Entity
    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setDateOfBirth(this.dateOfBirth);
        user.setGender(this.gender);
        user.setHeightCm(this.heightCm);
        user.setWeightKg(this.weightKg);
        user.setActivityLevel(this.activityLevel);
        return user;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public User.Gender getGender() {
        return gender;
    }
    
    public void setGender(User.Gender gender) {
        this.gender = gender;
    }
    
    public Double getHeightCm() {
        return heightCm;
    }
    
    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }
    
    public Double getWeightKg() {
        return weightKg;
    }
    
    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }
    
    public User.ActivityLevel getActivityLevel() {
        return activityLevel;
    }
    
    public void setActivityLevel(User.ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }
}

