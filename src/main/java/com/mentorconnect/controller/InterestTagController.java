package com.mentorconnect.controller;

import com.mentorconnect.dto.InterestTagRequest;
import com.mentorconnect.entity.InterestTag;
import com.mentorconnect.service.InterestTagService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class InterestTagController {

    private final InterestTagService service;

    public InterestTagController(
            InterestTagService service) {

        this.service = service;
    }

    @GetMapping
    public List<InterestTag> getAll() {

        return service.getAll();

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterestTag create(
            @Valid
            @RequestBody InterestTagRequest request) {

        return service.create(
                request.getName()
        );

    }
}