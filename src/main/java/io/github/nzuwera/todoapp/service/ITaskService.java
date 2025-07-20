package io.github.nzuwera.todoapp.service;

import io.github.nzuwera.todoapp.model.Task;
import reactor.core.publisher.Flux;

public interface ITaskService {
    Flux<Task> getTasks();
}
