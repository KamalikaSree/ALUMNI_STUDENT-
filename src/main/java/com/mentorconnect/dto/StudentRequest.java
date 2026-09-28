package com.mentorconnect.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class StudentRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String department;

    @NotNull
    @Min(1)
    @Max(6)
    private Integer yearOfStudy;

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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(Integer yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public List<String> getInterestTags() {
        return interestTags;
    }

    public void setInterestTags(List<String> interestTags) {
        this.interestTags = interestTags;
    }
}