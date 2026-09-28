package com.mentorconnect.controller;

import com.mentorconnect.dto.AlumniRequest;
import com.mentorconnect.entity.Alumni;
import com.mentorconnect.service.AlumniService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumni")
public class AlumniController {

    private final AlumniService service;

    public AlumniController(
            AlumniService service) {

        this.service = service;
    }

    @GetMapping
    public List<Alumni> getAll() {

        return service.getAll();

    }

    @GetMapping("/{id}")
    public Alumni getById(
            @PathVariable Long id) {

        return service.getById(id);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Alumni create(
            @Valid
            @RequestBody AlumniRequest request) {

        return service.create(request);

    }

    @PutMapping("/{id}")
    public Alumni update(
            @PathVariable Long id,
            @Valid
            @RequestBody AlumniRequest request) {

        return service.update(id, request);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id) {

        service.delete(id);

    }
}