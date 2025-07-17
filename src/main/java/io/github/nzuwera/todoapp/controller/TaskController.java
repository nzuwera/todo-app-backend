package io.github.nzuwera.todoapp.controller;

import io.github.nzuwera.todoapp.model.Task;
import io.github.nzuwera.todoapp.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

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
     * Streams Tasks as Server‑Sent Events (SSE). Clients will see one JSON
     * Task roughly every second.
     */
    @GetMapping(value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Task> streamTasks() {
        return taskService.streamAllTasks();
    }

    /**
     * GET /tasks?page=0&size=3
     *
     * page: zero‐based page index
     * size: number of items per page
     */
    @GetMapping("/page")
    public Flux<Task> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return taskService.findTasks(page, size);
    }

}
