package io.github.nzuwera.todoapp.exceptions;

public class TaskBusinessException extends RuntimeException {
    public TaskBusinessException(String message) {
        super(message);
    }
}
