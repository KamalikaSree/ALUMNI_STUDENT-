package com.mentorconnect.repository;

import com.mentorconnect.entity.MentoringSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentoringSessionRepository
        extends JpaRepository<MentoringSession, Long> {

    List<MentoringSession> findByPairId(Long pairId);

    long countByStatus(
            MentoringSession.SessionStatus status
    );
}