package io.github.nzuwera.todoapp.controller;

import io.github.nzuwera.todoapp.TaskFixtures;
import io.github.nzuwera.todoapp.model.Task;
import io.github.nzuwera.todoapp.service.ITaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ITaskService taskService;

    @Test
    void getTasks_ShouldReturnListOfTasks() {
        // Given
        Task task1 = TaskFixtures.createTask();
        List<Task> tasks = List.of(task1);

        when(taskService.getTasks()).thenReturn(Flux.fromIterable(tasks));

        // When & Then
        webTestClient.get()
                .uri("/v1/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Task.class)
                .value(responseList -> {
                    assert responseList.size() == 1;
                    Task responseTask = responseList.getFirst();
                    assert responseTask.getDescription().equals(task1.getDescription());
                    assert responseTask.isCompleted() == task1.isCompleted();
                });
    }

    @Test
    void createTask_ShouldReturnCreatedTask() {
        // Given
        Task taskToCreate = TaskFixtures.createTask();
        Task createdTask = TaskFixtures.createTask();

        when(taskService.createTask(any(Task.class))).thenReturn(Mono.just(createdTask));

        // When & Then
        webTestClient.post()
                .uri("/v1/tasks")
                .bodyValue(taskToCreate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .value(responseTask -> {
                    assert responseTask.getId().equals(createdTask.getId());
                    assert responseTask.getDescription().equals(createdTask.getDescription());
                    assert responseTask.isCompleted() == createdTask.isCompleted();
                });
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenTaskExists() {
        // Given
        Task task = TaskFixtures.createTask();
        String taskId = task.getId();

        when(taskService.getTasks()).thenReturn(Flux.just(task));

        // When & Then
        webTestClient.get()
                .uri("/v1/tasks/{taskId}", taskId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .value(responseTask -> {
                    assert responseTask.getId().equals(taskId);
                    assert responseTask.getDescription().equals(task.getDescription());
                    assert responseTask.isCompleted() == task.isCompleted();
                });
    }

    @Test
    void getTaskById_ShouldReturnEmptyResponse_WhenTaskDoesNotExist() {
        // Given
        String nonExistentTaskId = "non-existent-id";

        when(taskService.getTasks()).thenReturn(Flux.empty());

        // When & Then
        webTestClient.get()
                .uri("/v1/tasks/{taskId}", nonExistentTaskId)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void updateTaskById_ShouldReturnUpdatedTask() {
        // Given
        Task taskToUpdate = TaskFixtures.createTask();
        taskToUpdate.setDescription("Updated Task");
        taskToUpdate.setCompleted(true);

        String taskId = taskToUpdate.getId();

        when(taskService.updateTask(eq(taskId), any(Task.class))).thenReturn(Mono.just(taskToUpdate));

        // When & Then
        webTestClient.put()
                .uri("/v1/tasks/{taskId}", taskId)
                .bodyValue(taskToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .value(responseTask -> {
                    assert responseTask.getId().equals(taskId);
                    assert responseTask.getDescription().equals("Updated Task");
                    assert responseTask.isCompleted();
                });
    }

    @Test
    void deleteTaskById_ShouldReturnSuccess() {
        // Given
        String taskId = "task-id";

        when(taskService.deleteTask(taskId)).thenReturn(Mono.empty());

        // When & Then
        webTestClient.delete()
                .uri("/v1/tasks/{taskId}", taskId)
                .exchange()
                .expectStatus().isOk();

        verify(taskService).deleteTask(taskId);
    }
}
