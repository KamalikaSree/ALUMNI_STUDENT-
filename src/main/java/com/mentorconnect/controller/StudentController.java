package com.mentorconnect.controller;

import com.mentorconnect.dto.StudentRequest;
import com.mentorconnect.entity.Student;
import com.mentorconnect.service.StudentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(
            StudentService service) {

        this.service = service;
    }

    @GetMapping
    public List<Student> getAll() {

        return service.getAll();

    }

    @GetMapping("/{id}")
    public Student getById(
            @PathVariable Long id) {

        return service.getById(id);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student create(
            @Valid
            @RequestBody StudentRequest request) {

        return service.create(request);

    }

    @PutMapping("/{id}")
    public Student update(
            @PathVariable Long id,
            @Valid
            @RequestBody StudentRequest request) {

        return service.update(id, request);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id) {

        service.delete(id);

    }
}