package io.github.nzuwera.todoapp.controller;

import io.github.nzuwera.todoapp.TaskFixtures;
import io.github.nzuwera.todoapp.config.AbstractIntegrationTest;
import io.github.nzuwera.todoapp.config.WebTestClientConfig;
import io.github.nzuwera.todoapp.model.Task;
import io.github.nzuwera.todoapp.repository.TaskRepository;
import io.github.nzuwera.todoapp.service.ITaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

@Import(WebTestClientConfig.class)
class TaskControllerIntegrationTest extends AbstractIntegrationTest {


    private final WebTestClient webTestClient;
    private final ITaskService taskService;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskControllerIntegrationTest(WebTestClient webTestClient, ITaskService taskService, TaskRepository taskRepository) {
        this.webTestClient = webTestClient;
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    @LocalServerPort
    private int port;

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll().block();
    }


    @Test
    void shouldReturnEmptyTaskList() {
        webTestClient
                .get()
                .uri("http://localhost:" + port + "/v1/tasks")
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Task.class)
                .hasSize(0);
    }

    @Test
    void shouldReturnTaskList() {
        // Create and Save Task
        Task task = TaskFixtures.createTask();
        taskService.createTask(task)
                .block();

        webTestClient
                .get()
                .uri("http://localhost:" + port + "/v1/tasks")
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Task.class)
                .hasSize(1)
                .value(tasks -> {
                    Task firstTask = tasks.getFirst();
                    assertThat(firstTask.getDescription()).isEqualTo(task.getDescription());
                    assertThat(firstTask.getId()).isNotNull();
                    assertThat(firstTask.isCompleted()).isEqualTo(task.isCompleted());
                });
    }

    @Test
    void shouldCreateAndReturnTask() {
        // Given
        Task task = TaskFixtures.createTask();

        // When & Then
        webTestClient
                .post()
                .uri("http://localhost:" + port + "/v1/tasks")
                .accept(APPLICATION_STREAM_JSON)
                .bodyValue(task)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .value(createdTask -> {
                    assert createdTask.getDescription().equals(task.getDescription());
                    assert !createdTask.isCompleted();
                });
    }

    @Test
    void shouldGetTaskById() {
        // Given
        Task task = TaskFixtures.createTask();
        Task savedTask = taskService.createTask(task).block();

        // When & Then
        webTestClient
                .get()
                .uri("http://localhost:" + port + "/v1/tasks/" + savedTask.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .value(retrievedTask -> {
                    assertThat(retrievedTask.getId()).isEqualTo(savedTask.getId());
                    assertThat(retrievedTask.getDescription()).isEqualTo(savedTask.getDescription());
                    assertThat(retrievedTask.isCompleted()).isEqualTo(savedTask.isCompleted());
                });
    }

    @Test
    void shouldReturnEmptyWhenTaskDoesNotExist() {
        // Given
        String nonExistentTaskId = "non-existent-id";

        // When & Then
        webTestClient
                .get()
                .uri("http://localhost:" + port + "/v1/tasks/" + nonExistentTaskId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void shouldUpdateTaskById() {
        // Given
        Task task = TaskFixtures.createTask();
        Task savedTask = taskService.createTask(task).block();

        // Create updated task
        Task updatedTask = new Task();
        updatedTask.setDescription("Updated Task Description");
        updatedTask.setCompleted(true);

        // When & Then
        webTestClient
                .put()
                .uri("http://localhost:" + port + "/v1/tasks/" + savedTask.getId())
                .accept(APPLICATION_STREAM_JSON)
                .bodyValue(updatedTask)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .value(resultTask -> {
                    assertThat(resultTask.getId()).isEqualTo(savedTask.getId());
                    assertThat(resultTask.getDescription()).isEqualTo("Updated Task Description");
                    assertThat(resultTask.isCompleted()).isTrue();
                });
    }

    @Test
    void shouldDeleteTaskById() {
        // Given
        Task task = TaskFixtures.createTask();
        Task savedTask = taskService.createTask(task).block();

        // When - Delete the task
        webTestClient
                .delete()
                .uri("http://localhost:" + port + "/v1/tasks/" + savedTask.getId())
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk();

        // Then - Verify task is deleted
        webTestClient
                .get()
                .uri("http://localhost:" + port + "/v1/tasks/" + savedTask.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}
