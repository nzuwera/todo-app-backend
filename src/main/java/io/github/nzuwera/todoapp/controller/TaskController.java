package io.github.nzuwera.todoapp.controller;

import io.github.nzuwera.todoapp.model.Task;
import io.github.nzuwera.todoapp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    /**
     * GET /tasks/stream
     * Streams Tasks as Server‑Sent Events (SSE). Clients will see one JSON
     * Task roughly every second.
     * @return Flux<Task>
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all tasks", description = "Returns a list of tasks with optional pagination")
    public Flux<Task> streamTasks() {
        return taskService.getTasks();
    }

}
