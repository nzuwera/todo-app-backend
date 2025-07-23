package io.github.nzuwera.todoapp.controller;

import io.github.nzuwera.todoapp.config.OpenApiConfig;
import io.github.nzuwera.todoapp.model.Task;
import io.github.nzuwera.todoapp.service.ITaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
     * GET /tasks
     *
     * @return Flux<Task> - List of tasks. If no tasks are found, Flux<Task> will be empty.
     */
    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @Operation(summary = "Get all tasks", description = "Returns a list of tasks with optional pagination")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = OpenApiConfig.DESCRIPTION_OK, content = @Content()),
                    @ApiResponse(
                            responseCode = "404",
                            description = OpenApiConfig.DESCRIPTION_NOT_FOUND,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "500",
                            description = OpenApiConfig.DESCRIPTION_INTERNAL_SERVER_ERROR,
                            content = @Content)
            })
    public Flux<Task> getTasks() {
        return taskService.getTasks();
    }

    /**
     * POST /tasks
     *
     * @param task - Task to be created. Must not be null. All fields are optional.
     * @return Mono<Task> - Created task. If the task already exists, Mono<Task> will be empty.
     */
    @PostMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @Operation(summary = "Create a new task", description = "Creates a new task and returns it")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = OpenApiConfig.DESCRIPTION_OK, content = @Content()),
                    @ApiResponse(
                            responseCode = "400",
                            description = OpenApiConfig.DESCRIPTION_BAD_REQUEST,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = OpenApiConfig.DESCRIPTION_NOT_FOUND,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "500",
                            description = OpenApiConfig.DESCRIPTION_INTERNAL_SERVER_ERROR,
                            content = @Content)
            })
    public ResponseEntity<Mono<Task>> createTask(@Valid @RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    /**
     * GET /tasks/{taskId}
     *
     * @param taskId - Task Id to be retrieved. Must be a valid UUID.
     * @return Mono<Task> - Task with the given id. If no task is found, Mono<Task> will be empty.
     */
    @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a task by id", description = "Returns a task by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = OpenApiConfig.DESCRIPTION_OK, content = @Content()),
                    @ApiResponse(
                            responseCode = "404",
                            description = OpenApiConfig.DESCRIPTION_NOT_FOUND,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "500",
                            description = OpenApiConfig.DESCRIPTION_INTERNAL_SERVER_ERROR,
                            content = @Content)
            })
    public Mono<Task> getTaskById(@PathVariable String taskId) {
        return taskService.getTasks()
                .filter(task -> task.getId().equals(taskId))
                .next();
    }

    /**
     * PUT /tasks/{taskId}
     *
     * @param taskId - Task Id to be updated. Must be a valid UUID.
     * @param task   - Task to be updated. Must not be null. All fields are optional.
     * @return Mono<Task> - Updated task.
     */
    @PutMapping(value = "/{taskId}", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @Operation(summary = "Update task by Id", description = "Update existing task and returns it")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = OpenApiConfig.DESCRIPTION_OK, content = @Content()),
                    @ApiResponse(
                            responseCode = "400",
                            description = OpenApiConfig.DESCRIPTION_BAD_REQUEST,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = OpenApiConfig.DESCRIPTION_NOT_FOUND,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "500",
                            description = OpenApiConfig.DESCRIPTION_INTERNAL_SERVER_ERROR,
                            content = @Content)
            })
    public ResponseEntity<Mono<Task>> updateTaskById(@PathVariable String taskId, @Valid @RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(taskId, task));
    }

    /**
     * DELETE /tasks/{taskId}
     *
     * @param taskId - Task Id to be deleted. Must be a valid UUID.
     * @return Mono<Void> - Empty Mono<Void> indicating that the task was deleted.
     */
    @DeleteMapping(value = "/{taskId}", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @Operation(summary = "Delete task by Id", description = "Delete existing task and return confirmation message")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = OpenApiConfig.DESCRIPTION_OK, content = @Content()),
                    @ApiResponse(
                            responseCode = "404",
                            description = OpenApiConfig.DESCRIPTION_NOT_FOUND,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "500",
                            description = OpenApiConfig.DESCRIPTION_INTERNAL_SERVER_ERROR,
                            content = @Content)
            })
    public ResponseEntity<Mono<Void>> deleteTaskById(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.deleteTask(taskId));
    }

}
