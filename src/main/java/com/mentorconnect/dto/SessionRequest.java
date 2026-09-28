package com.mentorconnect.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class SessionRequest {

    @NotNull
    private Long pairId;

    @NotNull
    private LocalDateTime scheduledAt;

    @NotBlank
    private String topic;

    private String notes;

    public Long getPairId() {
        return pairId;
    }

    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}