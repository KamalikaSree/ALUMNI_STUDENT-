package com.mentorconnect.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class AlumniRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private String phone;

    private String company;

    private String designation;

    @NotNull
    @Min(1)
    private Integer maxConcurrentMentees;

    private String availableSlots;

    private Boolean active = true;

    private List<@NotBlank String> interestTags;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Integer getMaxConcurrentMentees() {
        return maxConcurrentMentees;
    }

    public void setMaxConcurrentMentees(Integer maxConcurrentMentees) {
        this.maxConcurrentMentees = maxConcurrentMentees;
    }

    public String getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(String availableSlots) {
        this.availableSlots = availableSlots;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<String> getInterestTags() {
        return interestTags;
    }

    public void setInterestTags(List<String> interestTags) {
        this.interestTags = interestTags;
    }
}