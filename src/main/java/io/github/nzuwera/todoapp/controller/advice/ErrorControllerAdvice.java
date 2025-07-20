package io.github.nzuwera.todoapp.controller.advice;

import io.github.nzuwera.todoapp.exceptions.TaskAlreadyExistException;
import io.github.nzuwera.todoapp.exceptions.TaskBusinessException;
import io.github.nzuwera.todoapp.exceptions.TaskNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorControllerAdvice {

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ProblemDetail> handleTaskNotFoundException(TaskNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        enrichProblemDetail(problemDetail, "Task Not Found");
        return Mono.just(problemDetail);
    }

    @ExceptionHandler(TaskAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ProblemDetail> handleTaskAlreadyExistException(TaskAlreadyExistException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        enrichProblemDetail(problemDetail, "Task Already Exists");
        return Mono.just(problemDetail);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ProblemDetail> handleValidationException(WebExchangeBindException ex) {
        Map<String, Object> validationErrors = new HashMap<>();

        // Handle field errors
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing + "; " + replacement
                ));

        // Handle global errors
        var globalErrors = ex.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value")
                .collect(Collectors.toList());

        validationErrors.put("fieldErrors", fieldErrors);
        if (!globalErrors.isEmpty()) {
            validationErrors.put("globalErrors", globalErrors);
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed"
        );
        problemDetail.setProperty("validationErrors", validationErrors);
        enrichProblemDetail(problemDetail, "Validation Error");

        return Mono.just(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ProblemDetail> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> violations = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        this::getPropertyPath,
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing + "; " + replacement
                ));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Constraint validation failed"
        );
        problemDetail.setProperty("violations", violations);
        enrichProblemDetail(problemDetail, "Constraint Violation");

        return Mono.just(problemDetail);
    }

    @ExceptionHandler(ServerWebInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ProblemDetail> handleServerWebInputException(ServerWebInputException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid request content"
        );
        problemDetail.setProperty("reason", ex.getReason());
        enrichProblemDetail(problemDetail, "Invalid Input");

        return Mono.just(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ProblemDetail> handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An error occurred - %s".formatted(ex.getMessage())
        );
        enrichProblemDetail(problemDetail, "Internal Server Error");
        return Mono.just(problemDetail);
    }

    @ExceptionHandler(TaskBusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ProblemDetail> handleGenericException(TaskBusinessException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Task processing failed with error - %s".formatted(ex.getMessage())
        );
        enrichProblemDetail(problemDetail, "Task Processing Error");
        return Mono.just(problemDetail);
    }

    private void enrichProblemDetail(ProblemDetail problemDetail, String title) {
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create("https://api.todoapp.com/errors/" + title.toLowerCase().replace(" ", "-")));
        problemDetail.setProperty("timestamp", Instant.now());
    }

    private String getPropertyPath(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        // Remove the method name from the path if present
        int lastDotIndex = propertyPath.lastIndexOf('.');
        return lastDotIndex != -1 ? propertyPath.substring(lastDotIndex + 1) : propertyPath;
    }
}