package com.mentorconnect.dto;

import jakarta.validation.constraints.NotNull;

public class MentorshipPairRequest {

    @NotNull
    private Long alumniId;

    @NotNull
    private Long studentId;

    public Long getAlumniId() {
        return alumniId;
    }

    public void setAlumniId(Long alumniId) {
        this.alumniId = alumniId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}