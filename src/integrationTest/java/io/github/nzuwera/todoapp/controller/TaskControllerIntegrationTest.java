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
                .accept(APPLICATION_JSON)
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
                .accept(APPLICATION_JSON)
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
                .accept(APPLICATION_JSON)
                .bodyValue(task)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .value(createdTask -> {
                    assert createdTask.getDescription().equals(task.getDescription());
                    assert !createdTask.isCompleted();
                });
    }
}