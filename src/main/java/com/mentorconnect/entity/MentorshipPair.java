package com.mentorconnect.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentorship_pairs")
public class MentorshipPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alumni_id")
    private Alumni alumni;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(nullable = false)
    private Integer matchScore;

    @Column(nullable = false)
    private LocalDateTime matchedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PairStatus status = PairStatus.ACTIVE;

    public MentorshipPair() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alumni getAlumni() {
        return alumni;
    }

    public void setAlumni(Alumni alumni) {
        this.alumni = alumni;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Integer matchScore) {
        this.matchScore = matchScore;
    }

    public LocalDateTime getMatchedAt() {
        return matchedAt;
    }

    public void setMatchedAt(LocalDateTime matchedAt) {
        this.matchedAt = matchedAt;
    }

    public PairStatus getStatus() {
        return status;
    }

    public void setStatus(PairStatus status) {
        this.status = status;
    }

    public enum PairStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }
}