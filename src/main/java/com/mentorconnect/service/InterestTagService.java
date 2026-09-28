package com.mentorconnect.service;

import com.mentorconnect.entity.InterestTag;
import com.mentorconnect.repository.InterestTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterestTagService {

    private final InterestTagRepository repository;

    public InterestTagService(
            InterestTagRepository repository) {

        this.repository = repository;
    }

    public InterestTag getOrCreate(String rawName) {

        String name = rawName.trim();

        return repository
                .findByNameIgnoreCase(name)
                .orElseGet(() ->
                        repository.save(
                                new InterestTag(name)
                        )
                );
    }

    public List<InterestTag> getAll() {

        return repository.findAll();

    }

    public InterestTag create(String name) {

        return getOrCreate(name);

    }
}