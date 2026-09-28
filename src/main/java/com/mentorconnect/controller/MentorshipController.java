package com.mentorconnect.controller;

import com.mentorconnect.dto.MatchSuggestionDto;
import com.mentorconnect.dto.MentorshipPairRequest;
import com.mentorconnect.dto.StatusRequest;
import com.mentorconnect.entity.MentorshipPair;
import com.mentorconnect.service.MentorshipService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MentorshipController {

    private final MentorshipService service;

    public MentorshipController(
            MentorshipService service) {

        this.service = service;
    }

    @GetMapping
    public List<MentorshipPair> getAllPairs() {

        return service.getAllPairs();

    }

    @GetMapping("/{id}")
    public MentorshipPair getPair(
            @PathVariable Long id) {

        return service.getPair(id);

    }

    @GetMapping("/suggestions/student/{studentId}")
    public List<MatchSuggestionDto> suggest(
            @PathVariable Long studentId) {

        return service.suggest(studentId);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipPair create(
            @Valid
            @RequestBody MentorshipPairRequest request) {

        return service.createPair(request);

    }

    @PutMapping("/{id}/status")
    public MentorshipPair updateStatus(
            @PathVariable Long id,
            @Valid
            @RequestBody StatusRequest request) {

        return service.updateStatus(
                id,
                request.getStatus()
        );

    }
}