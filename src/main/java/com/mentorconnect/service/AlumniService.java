package com.mentorconnect.service;

import com.mentorconnect.dto.AlumniRequest;
import com.mentorconnect.entity.Alumni;
import com.mentorconnect.entity.InterestTag;
import com.mentorconnect.repository.AlumniRepository;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlumniService {

    private final AlumniRepository repository;

    private final InterestTagService tagService;

    public AlumniService(
            AlumniRepository repository,
            InterestTagService tagService) {

        this.repository = repository;
        this.tagService = tagService;
    }

    public List<Alumni> getAll() {

        return repository.findAll();

    }

    public Alumni getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Alumni not found with id " + id
                        )
                );
    }

    public Alumni create(AlumniRequest request) {

        repository.findByEmail(request.getEmail())
                .ifPresent(a -> {
                    throw new IllegalArgumentException(
                            "An alumni with this email already exists"
                    );
                });

        Alumni alumni = new Alumni();

        apply(alumni, request);

        return repository.save(alumni);
    }

    public Alumni update(
            Long id,
            AlumniRequest request) {

        Alumni alumni = getById(id);

        repository.findByEmail(request.getEmail())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        throw new IllegalArgumentException(
                                "Another alumni already uses this email"
                        );
                    }
                });

        apply(alumni, request);

        return repository.save(alumni);
    }

    public void delete(Long id) {

        repository.delete(getById(id));

    }

    private void apply(
            Alumni alumni,
            AlumniRequest request) {

        alumni.setName(
                request.getName().trim()
        );

        alumni.setEmail(
                request.getEmail()
                        .trim()
                        .toLowerCase()
        );

        alumni.setPhone(
                request.getPhone()
        );

        alumni.setCompany(
                request.getCompany()
        );

        alumni.setDesignation(
                request.getDesignation()
        );

        alumni.setMaxConcurrentMentees(
                request.getMaxConcurrentMentees()
        );

        alumni.setAvailableSlots(
                request.getAvailableSlots()
        );

        alumni.setActive(
                request.getActive() == null
                        || request.getActive()
        );

        Set<InterestTag> tags =
                Optional.ofNullable(
                                request.getInterestTags()
                        )
                        .orElseGet(List::of)
                        .stream()
                        .map(tagService::getOrCreate)
                        .collect(Collectors.toSet());

        alumni.setInterestTags(tags);
    }
}