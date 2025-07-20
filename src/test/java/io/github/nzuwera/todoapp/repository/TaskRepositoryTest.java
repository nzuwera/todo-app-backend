package io.github.nzuwera.todoapp.repository;

import io.github.nzuwera.todoapp.TaskEntityFixtures;
import io.github.nzuwera.todoapp.entity.TaskEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskRepositoryTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskEntity taskEntity;

    @BeforeEach
    void setUp() {
        taskEntity = TaskEntityFixtures.createTaskEntity();
    }

    @Test
    void findByDescription_ShouldReturnTask_WhenTaskExists() {
        // Given
        String description = "Test Task";
        when(taskRepository.findByDescription(eq(description)))
                .thenReturn(Mono.just(taskEntity));

        // When
        Mono<TaskEntity> result = taskRepository.findByDescription(description);

        // Then
        StepVerifier.create(result)
                .expectNext(taskEntity)
                .verifyComplete();
    }

    @Test
    void findByDescription_ShouldReturnEmptyMono_WhenTaskDoesNotExist() {
        // Given
        String description = "Nonexistent Task";
        when(taskRepository.findByDescription(eq(description)))
                .thenReturn(Mono.empty());

        // When
        Mono<TaskEntity> result = taskRepository.findByDescription(description);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void save_ShouldReturnSavedTask() {
        // Given
        when(taskRepository.save(any(TaskEntity.class)))
                .thenReturn(Mono.just(taskEntity));

        // When

        Mono<TaskEntity> result = taskRepository.save(taskEntity);

        // Then
        StepVerifier.create(result)
                .expectNext(taskEntity)
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnTask_WhenTaskExists() {
        // Given
        UUID id = taskEntity.getId();
        when(taskRepository.findById(eq(id)))
                .thenReturn(Mono.just(taskEntity));

        // When
        Mono<TaskEntity> result = taskRepository.findById(id);

        // Then
        StepVerifier.create(result)
                .expectNext(taskEntity)
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnEmptyMono_WhenTaskDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(eq(id)))
                .thenReturn(Mono.empty());

        // When
        Mono<TaskEntity> result = taskRepository.findById(id);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }
}