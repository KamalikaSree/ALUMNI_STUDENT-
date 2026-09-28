package com.mentorconnect.dto;

public record EngagementReportDto(

        Long pairId,

        String alumniName,

        String studentName,

        Integer matchScore,

        Long totalSessions,

        Long completedSessions,

        Long scheduledSessions,

        Long cancelledSessions

) {
}