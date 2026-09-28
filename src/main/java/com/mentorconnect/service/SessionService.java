package com.mentorconnect.service;

import com.mentorconnect.dto.SessionRequest;
import com.mentorconnect.entity.MentoringSession;
import com.mentorconnect.entity.MentorshipPair;
import com.mentorconnect.repository.MentoringSessionRepository;
import com.mentorconnect.repository.MentorshipPairRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {

    private final MentoringSessionRepository sessionRepository;

    private final MentorshipPairRepository pairRepository;

    public SessionService(
            MentoringSessionRepository sessionRepository,
            MentorshipPairRepository pairRepository) {

        this.sessionRepository = sessionRepository;
        this.pairRepository = pairRepository;
    }

    public List<MentoringSession> getAll() {

        return sessionRepository.findAll();

    }

    public MentoringSession create(
            SessionRequest request) {

        MentorshipPair pair =
                pairRepository
                        .findById(request.getPairId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Mentorship pair not found with id "
                                                + request.getPairId()
                                )
                        );

        if (pair.getStatus()
                != MentorshipPair.PairStatus.ACTIVE) {

            throw new IllegalArgumentException(
                    "Sessions can be scheduled only for an ACTIVE mentorship pair"
            );
        }

        MentoringSession session =
                new MentoringSession();

        session.setPair(pair);

        session.setScheduledAt(
                request.getScheduledAt()
        );

        session.setTopic(
                request.getTopic().trim()
        );

        session.setNotes(
                request.getNotes()
        );

        session.setStatus(
                MentoringSession.SessionStatus.SCHEDULED
        );

        return sessionRepository.save(session);
    }

    public MentoringSession getById(
            Long id) {

        return sessionRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Session not found with id "
                                        + id
                        )
                );
    }

    public MentoringSession updateStatus(
            Long id,
            String rawStatus) {

        MentoringSession session =
                getById(id);

        try {

            session.setStatus(
                    MentoringSession.SessionStatus
                            .valueOf(
                                    rawStatus
                                            .trim()
                                            .toUpperCase()
                            )
            );

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Status must be SCHEDULED, COMPLETED, or CANCELLED"
            );
        }

        return sessionRepository.save(session);
    }
}