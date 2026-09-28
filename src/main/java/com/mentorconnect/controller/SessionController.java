package com.mentorconnect.controller;

import com.mentorconnect.dto.SessionRequest;
import com.mentorconnect.dto.StatusRequest;
import com.mentorconnect.entity.MentoringSession;
import com.mentorconnect.service.SessionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService service;

    public SessionController(
            SessionService service) {

        this.service = service;
    }

    @GetMapping
    public List<MentoringSession> getAll() {

        return service.getAll();

    }

    @GetMapping("/{id}")
    public MentoringSession getById(
            @PathVariable Long id) {

        return service.getById(id);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MentoringSession create(
            @Valid
            @RequestBody SessionRequest request) {

        return service.create(request);

    }

    @PutMapping("/{id}/status")
    public MentoringSession updateStatus(
            @PathVariable Long id,
            @Valid
            @RequestBody StatusRequest request) {

        return service.updateStatus(
                id,
                request.getStatus()
        );

    }
}