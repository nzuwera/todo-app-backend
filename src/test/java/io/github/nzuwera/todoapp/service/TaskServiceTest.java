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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskEntity taskEntity;
    private UUID taskId;
    private Instant now;
    private Task task;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        now = Instant.now();

        // Setup TaskEntity
        taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setDescription("Test task");
        taskEntity.setCompleted(false);
        taskEntity.setCreatedAt(now);
        taskEntity.setUpdatedAt(now);

        // Setup Task
        task = new Task();
        task.setId(taskId.toString());
        task.setDescription("Test task");
        task.setCompleted(false);
        task.setCreatedAt(now.toString());
        task.setUpdatedAt(now.toString());
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

    @Test
    void createTask_ShouldReturnCreatedTask_WhenTaskDoesNotExist() {
        // Given
        when(taskRepository.findByDescription(task.getDescription())).thenReturn(Mono.empty());
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(Mono.just(taskEntity));

        // When
        Mono<Task> result = taskService.createTask(task);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(createdTask ->
                        createdTask.getId().equals(taskId.toString()) &&
                        createdTask.getDescription().equals(task.getDescription()) &&
                        createdTask.isCompleted() == task.isCompleted()
                )
                .verifyComplete();

        verify(taskRepository).findByDescription(task.getDescription());
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    void createTask_ShouldReturnError_WhenTaskAlreadyExists() {
        // Given
        when(taskRepository.findByDescription(task.getDescription())).thenReturn(Mono.just(taskEntity));

        // When
        Mono<Task> result = taskService.createTask(task);

        // Then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(taskRepository).findByDescription(task.getDescription());
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask_WhenTaskExists() {
        // Given
        TaskEntity updatedEntity = new TaskEntity();
        updatedEntity.setId(taskId);
        updatedEntity.setDescription("Updated task");
        updatedEntity.setCompleted(true);
        updatedEntity.setCreatedAt(now);
        updatedEntity.setUpdatedAt(now);

        Task updatedTask = new Task();
        updatedTask.setId(taskId.toString());
        updatedTask.setDescription("Updated task");
        updatedTask.setCompleted(true);
        updatedTask.setCreatedAt(now.toString());
        updatedTask.setUpdatedAt(now.toString());

        when(taskRepository.findById(taskId)).thenReturn(Mono.just(taskEntity));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(Mono.just(updatedEntity));

        // When
        Mono<Task> result = taskService.updateTask(taskId.toString(), updatedTask);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(task ->
                        task.getId().equals(taskId.toString()) &&
                        task.getDescription().equals("Updated task") &&
                        task.isCompleted()
                )
                .verifyComplete();

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    void updateTask_ShouldReturnEmptyMono_WhenTaskDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(taskRepository.findById(nonExistentId)).thenReturn(Mono.empty());

        // When
        Mono<Task> result = taskService.updateTask(nonExistentId.toString(), task);

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(taskRepository).findById(nonExistentId);
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void deleteTask_ShouldDeleteTask_WhenTaskExists() {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Mono.just(taskEntity));
        when(taskRepository.delete(taskEntity)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = taskService.deleteTask(taskId.toString());

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(taskRepository).findById(taskId);
        verify(taskRepository).delete(taskEntity);
    }

    @Test
    void deleteTask_ShouldReturnEmptyMono_WhenTaskDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(taskRepository.findById(nonExistentId)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = taskService.deleteTask(nonExistentId.toString());

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(taskRepository).findById(nonExistentId);
        verify(taskRepository, never()).delete(any(TaskEntity.class));
    }
}
