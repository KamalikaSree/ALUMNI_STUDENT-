package com.mentorconnect.service;

import com.mentorconnect.dto.DashboardSummaryDto;
import com.mentorconnect.dto.EngagementReportDto;
import com.mentorconnect.entity.MentoringSession;
import com.mentorconnect.entity.MentorshipPair;
import com.mentorconnect.repository.AlumniRepository;
import com.mentorconnect.repository.StudentRepository;
import com.mentorconnect.repository.MentorshipPairRepository;
import com.mentorconnect.repository.MentoringSessionRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final AlumniRepository alumniRepository;

    private final StudentRepository studentRepository;

    private final MentorshipPairRepository pairRepository;

    private final MentoringSessionRepository sessionRepository;

    public ReportService(
            AlumniRepository alumniRepository,
            StudentRepository studentRepository,
            MentorshipPairRepository pairRepository,
            MentoringSessionRepository sessionRepository) {

        this.alumniRepository = alumniRepository;
        this.studentRepository = studentRepository;
        this.pairRepository = pairRepository;
        this.sessionRepository = sessionRepository;
    }

    public DashboardSummaryDto dashboard() {

        return new DashboardSummaryDto(

                alumniRepository.count(),

                studentRepository.count(),

                pairRepository.countByStatus(
                        MentorshipPair.PairStatus.ACTIVE
                ),

                sessionRepository.count(),

                sessionRepository.countByStatus(
                        MentoringSession.SessionStatus.COMPLETED
                )
        );
    }

    public List<EngagementReportDto> engagement() {

        return pairRepository.findAll()
                .stream()
                .map(pair -> {

                    List<MentoringSession> sessions =
                            sessionRepository
                                    .findByPairId(
                                            pair.getId()
                                    );

                    long completed =
                            sessions.stream()
                                    .filter(s ->
                                            s.getStatus()
                                                    == MentoringSession
                                                    .SessionStatus
                                                    .COMPLETED
                                    )
                                    .count();

                    long scheduled =
                            sessions.stream()
                                    .filter(s ->
                                            s.getStatus()
                                                    == MentoringSession
                                                    .SessionStatus
                                                    .SCHEDULED
                                    )
                                    .count();

                    long cancelled =
                            sessions.stream()
                                    .filter(s ->
                                            s.getStatus()
                                                    == MentoringSession
                                                    .SessionStatus
                                                    .CANCELLED
                                    )
                                    .count();

                    return new EngagementReportDto(

                            pair.getId(),

                            pair.getAlumni()
                                    .getName(),

                            pair.getStudent()
                                    .getName(),

                            pair.getMatchScore(),

                            (long) sessions.size(),

                            completed,

                            scheduled,

                            cancelled
                    );
                })
                .toList();
    }
}