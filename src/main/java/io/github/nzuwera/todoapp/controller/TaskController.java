package io.github.nzuwera.todoapp.controller;

import io.github.nzuwera.todoapp.model.Task;
import io.github.nzuwera.todoapp.service.ITaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final ITaskService taskService;

    /**
     * GET /tasks/stream
     * Streams Tasks as Server‑Sent Events (SSE). Clients will see one JSON
     * Task roughly every second.
     * @return Flux<Task>
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all tasks", description = "Returns a list of tasks with optional pagination")
    public Flux<Task> getTasks() {
        return taskService.getTasks();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new task", description = "Creates a new task and returns it")
    public ResponseEntity<Mono<Task>> createTask(@Valid @RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

}
