package com.mentorconnect.dto;

public record DashboardSummaryDto(

        long alumniCount,

        long studentCount,

        long activePairCount,

        long totalSessionCount,

        long completedSessionCount

) {
}