package com.mentorconnect.controller;

import com.mentorconnect.dto.ErrorResponse;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            IllegalArgumentException.class
    )
    @ResponseStatus(
            HttpStatus.BAD_REQUEST
    )
    public ErrorResponse handleBadRequest(
            IllegalArgumentException ex) {

        return new ErrorResponse(

                LocalDateTime.now(),

                400,

                ex.getMessage()
        );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    @ResponseStatus(
            HttpStatus.BAD_REQUEST
    )
    public ErrorResponse handleValidation(
            MethodArgumentNotValidException ex) {

        String message =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error ->
                                error.getField()
                                        + ": "
                                        + error.getDefaultMessage()
                        )
                        .collect(
                                Collectors.joining("; ")
                        );

        return new ErrorResponse(

                LocalDateTime.now(),

                400,

                message
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(
            HttpStatus.INTERNAL_SERVER_ERROR
    )
    public ErrorResponse handleOther(
            Exception ex) {

        return new ErrorResponse(

                LocalDateTime.now(),

                500,

                "Unexpected server error"
        );
    }
}