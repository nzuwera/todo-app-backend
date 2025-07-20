package io.github.nzuwera.todoapp.service;

import io.github.nzuwera.todoapp.model.Task;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITaskService {
    Flux<Task> getTasks();

    Mono<Task> createTask(@Valid Task task);
}
