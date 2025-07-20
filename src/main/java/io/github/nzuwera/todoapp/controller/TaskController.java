package io.github.nzuwera.todoapp.controller;

import io.github.nzuwera.todoapp.model.Task;
import io.github.nzuwera.todoapp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<String> index(){
        return Mono.just("Welcome to todoapp");
    }

    /**
     * GET /tasks/stream
     * Streams Tasks as Server‑Sent Events (SSE). Clients will see one JSON
     * Task roughly every second.
     * @return Flux<Task>
     */
    @GetMapping(value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Get all tasks", description = "Returns a list of tasks with optional pagination")
    public Flux<Task> streamTasks() {
        return taskService.streamAllTasks();
    }

    /**
     * GET /tasks?page=0&size=3. Returns a Flux<Task> for the given page and size.
     * @param page zero‐based page index
     * @param size number of items per page
     * @return Flux<Task>
     */
    @GetMapping("/page")
    public Flux<Task> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return taskService.findTasks(page, size);
    }

}
