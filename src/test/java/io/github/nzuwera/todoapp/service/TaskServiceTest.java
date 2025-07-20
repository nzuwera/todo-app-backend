package io.github.nzuwera.todoapp.service;

import io.github.nzuwera.todoapp.repository.TaskRepository;
import io.github.nzuwera.todoapp.entity.TaskEntity;
import io.github.nzuwera.todoapp.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskEntity taskEntity;
    private UUID taskId;
    private Instant now;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        now = Instant.now();
        taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setDescription("Test task");
        taskEntity.setCompleted(false);
        taskEntity.setCreatedAt(now);
        taskEntity.setUpdatedAt(now);
    }

    @Test
    void getTasks_ShouldReturnMappedTasks() {
        // Given
        when(taskRepository.findAll()).thenReturn(Flux.just(taskEntity));

        // When
        Flux<Task> result = taskService.getTasks();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(task ->
                        task.getId().equals(taskId.toString()) &&
                                task.getDescription().equals(taskEntity.getDescription()) &&
                                task.isCompleted() == taskEntity.isCompleted() &&
                                task.getCreatedAt().equals(taskEntity.getCreatedAt().toString()) &&
                                task.getUpdatedAt().equals(taskEntity.getUpdatedAt().toString())
                )
                .verifyComplete();
    }

    @Test
    void getTasks_ShouldReturnEmptyFlux_WhenNoTasks() {
        // Given
        when(taskRepository.findAll()).thenReturn(Flux.empty());

        // When
        Flux<Task> result = taskService.getTasks();

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}