package com.mentorconnect.dto;

import jakarta.validation.constraints.NotBlank;

public class InterestTagRequest {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}