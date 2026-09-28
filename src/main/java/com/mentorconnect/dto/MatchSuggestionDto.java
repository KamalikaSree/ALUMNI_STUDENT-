package com.mentorconnect.dto;

import java.util.List;

public record MatchSuggestionDto(

        Long alumniId,

        String alumniName,

        String company,

        String designation,

        String availableSlots,

        Integer currentMentees,

        Integer maxConcurrentMentees,

        Integer overlapCount,

        List<String> overlappingTags

) {
}