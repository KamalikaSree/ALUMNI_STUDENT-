package com.mentorconnect.service;

import com.mentorconnect.dto.MatchSuggestionDto;
import com.mentorconnect.dto.MentorshipPairRequest;
import com.mentorconnect.entity.Alumni;
import com.mentorconnect.entity.InterestTag;
import com.mentorconnect.entity.MentorshipPair;
import com.mentorconnect.entity.Student;
import com.mentorconnect.repository.AlumniRepository;
import com.mentorconnect.repository.MentorshipPairRepository;
import com.mentorconnect.repository.StudentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MentorshipService {

    private final AlumniRepository alumniRepository;

    private final StudentRepository studentRepository;

    private final MentorshipPairRepository pairRepository;

    public MentorshipService(
            AlumniRepository alumniRepository,
            StudentRepository studentRepository,
            MentorshipPairRepository pairRepository) {

        this.alumniRepository = alumniRepository;
        this.studentRepository = studentRepository;
        this.pairRepository = pairRepository;
    }

    /*
     * SMART MATCHING
     */
    public List<MatchSuggestionDto> suggest(
            Long studentId) {

        Student student =
                studentRepository.findById(studentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Student not found with id "
                                                + studentId
                                )
                        );

        Set<String> studentTags =
                normalizedTags(
                        student.getInterestTags()
                );

        return alumniRepository.findAll()
                .stream()

                // Only active alumni
                .filter(a ->
                        Boolean.TRUE.equals(
                                a.getActive()
                        )
                )

                .map(alumni -> {

                    Set<String> alumniTags =
                            normalizedTags(
                                    alumni.getInterestTags()
                            );

                    List<String> overlap =
                            alumniTags.stream()
                                    .filter(
                                            studentTags::contains
                                    )
                                    .sorted()
                                    .toList();

                    long current =
                            pairRepository
                                    .countByAlumniIdAndStatus(
                                            alumni.getId(),
                                            MentorshipPair.PairStatus.ACTIVE
                                    );

                    return new MatchSuggestionDto(

                            alumni.getId(),

                            alumni.getName(),

                            alumni.getCompany(),

                            alumni.getDesignation(),

                            alumni.getAvailableSlots(),

                            (int) current,

                            alumni.getMaxConcurrentMentees(),

                            overlap.size(),

                            overlap
                    );
                })

                // Must have at least one common tag
                .filter(
                        m -> m.overlapCount() > 0
                )

                // Must have available capacity
                .filter(
                        m -> m.currentMentees()
                                < m.maxConcurrentMentees()
                )

                // Highest overlap first
                .sorted(
                        Comparator
                                .comparing(
                                        MatchSuggestionDto
                                                ::overlapCount
                                )
                                .reversed()

                                .thenComparing(
                                        MatchSuggestionDto
                                                ::alumniName
                                )
                )

                .toList();
    }

    /*
     * CREATE MENTORSHIP PAIR
     */
    @Transactional
    public MentorshipPair createPair(
            MentorshipPairRequest request) {

        Alumni alumni =
                alumniRepository
                        .findById(request.getAlumniId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Alumni not found with id "
                                                + request.getAlumniId()
                                )
                        );

        Student student =
                studentRepository
                        .findById(request.getStudentId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Student not found with id "
                                                + request.getStudentId()
                                )
                        );

        /*
         * RULE 1
         * Alumni must be active.
         */
        if (!Boolean.TRUE.equals(
                alumni.getActive())) {

            throw new IllegalArgumentException(
                    "This alumni is not currently available for mentoring"
            );
        }

        /*
         * RULE 2
         * Maximum concurrent mentee limit.
         */
        long current =
                pairRepository
                        .countByAlumniIdAndStatus(
                                alumni.getId(),
                                MentorshipPair.PairStatus.ACTIVE
                        );

        if (current >=
                alumni.getMaxConcurrentMentees()) {

            throw new IllegalArgumentException(
                    "Mentor capacity exceeded. Maximum concurrent mentees: "
                            + alumni.getMaxConcurrentMentees()
            );
        }

        /*
         * RULE 3
         * Avoid duplicate active pair.
         */
        if (pairRepository
                .existsByAlumniIdAndStudentIdAndStatus(
                        alumni.getId(),
                        student.getId(),
                        MentorshipPair.PairStatus.ACTIVE
                )) {

            throw new IllegalArgumentException(
                    "This student is already actively matched with this alumni"
            );
        }

        /*
         * RULE 4
         * At least one common interest.
         */
        Set<String> studentTags =
                normalizedTags(
                        student.getInterestTags()
                );

        Set<String> alumniTags =
                normalizedTags(
                        alumni.getInterestTags()
                );

        List<String> overlap =
                studentTags.stream()
                        .filter(alumniTags::contains)
                        .toList();

        if (overlap.isEmpty()) {

            throw new IllegalArgumentException(
                    "Cannot create match because there is no overlapping interest tag"
            );
        }

        /*
         * CREATE PAIR
         */
        MentorshipPair pair =
                new MentorshipPair();

        pair.setAlumni(alumni);

        pair.setStudent(student);

        pair.setMatchScore(
                overlap.size()
        );

        pair.setMatchedAt(
                LocalDateTime.now()
        );

        pair.setStatus(
                MentorshipPair.PairStatus.ACTIVE
        );

        return pairRepository.save(pair);
    }

    public List<MentorshipPair> getAllPairs() {

        return pairRepository.findAll();

    }

    public MentorshipPair getPair(Long id) {

        return pairRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Mentorship pair not found with id "
                                        + id
                        )
                );
    }

    public MentorshipPair updateStatus(
            Long id,
            String rawStatus) {

        MentorshipPair pair =
                getPair(id);

        try {

            pair.setStatus(
                    MentorshipPair.PairStatus.valueOf(
                            rawStatus
                                    .trim()
                                    .toUpperCase()
                    )
            );

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Status must be ACTIVE, COMPLETED, or CANCELLED"
            );
        }

        return pairRepository.save(pair);
    }

    /*
     * Converts tags to lowercase so:
     *
     * Java
     * JAVA
     * java
     *
     * are treated as the same tag.
     */
    private Set<String> normalizedTags(
            Set<InterestTag> tags) {

        return tags.stream()
                .map(InterestTag::getName)
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }
}