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

import java.util.List;

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
}