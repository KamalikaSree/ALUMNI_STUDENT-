package com.mentorconnect.service;

import com.mentorconnect.dto.StudentRequest;
import com.mentorconnect.entity.InterestTag;
import com.mentorconnect.entity.Student;
import com.mentorconnect.repository.StudentRepository;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository repository;

    private final InterestTagService tagService;

    public StudentService(
            StudentRepository repository,
            InterestTagService tagService) {

        this.repository = repository;
        this.tagService = tagService;
    }

    public List<Student> getAll() {

        return repository.findAll();

    }

    public Student getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found with id " + id
                        )
                );
    }

    public Student create(StudentRequest request) {

        repository.findByEmail(request.getEmail())
                .ifPresent(s -> {

                    throw new IllegalArgumentException(
                            "A student with this email already exists"
                    );

                });

        Student student = new Student();

        apply(student, request);

        return repository.save(student);
    }

    public Student update(
            Long id,
            StudentRequest request) {

        Student student = getById(id);

        repository.findByEmail(request.getEmail())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        throw new IllegalArgumentException(
                                "Another student already uses this email"
                        );
                    }

                });

        apply(student, request);

        return repository.save(student);
    }

    public void delete(Long id) {

        repository.delete(getById(id));

    }

    private void apply(
            Student student,
            StudentRequest request) {

        student.setName(
                request.getName().trim()
        );

        student.setEmail(
                request.getEmail()
                        .trim()
                        .toLowerCase()
        );

        student.setDepartment(
                request.getDepartment().trim()
        );

        student.setYearOfStudy(
                request.getYearOfStudy()
        );

        Set<InterestTag> tags =
                Optional.ofNullable(
                                request.getInterestTags()
                        )
                        .orElseGet(List::of)
                        .stream()
                        .map(tagService::getOrCreate)
                        .collect(Collectors.toSet());

        student.setInterestTags(tags);
    }
}