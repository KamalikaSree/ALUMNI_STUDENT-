package com.mentorconnect.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "alumni")
public class Alumni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private String company;

    private String designation;

    @Column(nullable = false)
    private Integer maxConcurrentMentees = 2;

    @Column(length = 500)
    private String availableSlots;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "alumni_interest_tags",
            joinColumns = @JoinColumn(name = "alumni_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<InterestTag> interestTags = new HashSet<>();

    public Alumni() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Integer getMaxConcurrentMentees() {
        return maxConcurrentMentees;
    }

    public void setMaxConcurrentMentees(Integer maxConcurrentMentees) {
        this.maxConcurrentMentees = maxConcurrentMentees;
    }

    public String getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(String availableSlots) {
        this.availableSlots = availableSlots;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<InterestTag> getInterestTags() {
        return interestTags;
    }

    public void setInterestTags(Set<InterestTag> interestTags) {
        this.interestTags = interestTags;
    }
}