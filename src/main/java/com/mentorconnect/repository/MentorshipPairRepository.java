package com.mentorconnect.repository;

import com.mentorconnect.entity.MentorshipPair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorshipPairRepository
        extends JpaRepository<MentorshipPair, Long> {

    long countByAlumniIdAndStatus(
            Long alumniId,
            MentorshipPair.PairStatus status
    );

    boolean existsByAlumniIdAndStudentIdAndStatus(
            Long alumniId,
            Long studentId,
            MentorshipPair.PairStatus status
    );

    List<MentorshipPair> findByStudentId(Long studentId);

    List<MentorshipPair> findByAlumniId(Long alumniId);

    List<MentorshipPair> findByStatus(
            MentorshipPair.PairStatus status
    );

    long countByStatus(
            MentorshipPair.PairStatus status
    );
}