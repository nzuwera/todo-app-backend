package io.github.nzuwera.todoapp.repository;

import io.github.nzuwera.todoapp.TaskEntityFixtures;
import io.github.nzuwera.todoapp.config.AbstractIntegrationTest;
import io.github.nzuwera.todoapp.entity.TaskEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    private TaskEntity testTask;

    @BeforeEach
    void setUp() {
        // Clean up the repository before each test
        taskRepository.deleteAll()
                .block(); // Block for test setup

        // Create and save a test task
        testTask = TaskEntityFixtures.createTaskEntity();
        testTask = taskRepository.save(testTask)
                .block(); // Block for test setup and get the saved entity with generated ID
    }

    @Test
    void findByDescription_ShouldReturnTask_WhenTaskExists() {
        // When
        Mono<TaskEntity> result = taskRepository.findByDescription(testTask.getDescription());

        // Then
        StepVerifier.create(result)
                .expectNextMatches(task -> 
                    task.getId().equals(testTask.getId()) &&
                    task.getDescription().equals(testTask.getDescription()) &&
                    task.isCompleted() == testTask.isCompleted())
                .verifyComplete();
    }

    @Test
    void findByDescription_ShouldReturnEmptyMono_WhenTaskDoesNotExist() {
        // When
        Mono<TaskEntity> result = taskRepository.findByDescription("Non-existent Task");

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void save_ShouldCreateNewTask() {
        // Given
        TaskEntity newTask = TaskEntityFixtures.createTaskEntity();
        newTask.setDescription("New Test Task");

        // When
        TaskEntity savedTask = taskRepository.save(newTask)
                .block(); // Block to get the saved entity with generated ID

        // Then
        // Verify the task was saved with the correct properties
        assertNotNull(savedTask.getId());
        assertEquals("New Test Task", savedTask.getDescription());
        assertFalse(savedTask.isCompleted());

        // Verify the task was actually saved to the database
        StepVerifier.create(taskRepository.findById(savedTask.getId()))
                .expectNextMatches(task -> task.getDescription().equals("New Test Task"))
                .verifyComplete();
    }

    @Test
    void update_ShouldUpdateExistingTask() {
        // Given
        testTask.setDescription("Updated Description");
        testTask.setCompleted(true);

        // When
        TaskEntity updatedTask = taskRepository.save(testTask)
                .block(); // Block to get the updated entity

        // Then
        // Verify the task was updated with the correct properties
        assertEquals(testTask.getId(), updatedTask.getId());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertTrue(updatedTask.isCompleted());

        // Verify the task was actually updated in the database
        TaskEntity retrievedTask = taskRepository.findById(testTask.getId())
                .block();
        assertNotNull(retrievedTask);
        assertEquals("Updated Description", retrievedTask.getDescription());
        assertTrue(retrievedTask.isCompleted());
    }

    @Test
    void findById_ShouldReturnTask_WhenTaskExists() {
        // When
        Mono<TaskEntity> result = taskRepository.findById(testTask.getId());

        // Then
        StepVerifier.create(result)
                .expectNextMatches(task -> 
                    task.getId().equals(testTask.getId()) &&
                    task.getDescription().equals(testTask.getDescription()))
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnEmptyMono_WhenTaskDoesNotExist() {
        // When
        Mono<TaskEntity> result = taskRepository.findById(UUID.randomUUID());

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void deleteById_ShouldRemoveTask() {
        // When
        taskRepository.deleteById(testTask.getId()).block();

        // Then
        // Verify the task was actually deleted from the database
        TaskEntity deletedTask = taskRepository.findById(testTask.getId()).block();
        assertNull(deletedTask);

        // Alternative approach using StepVerifier
        StepVerifier.create(taskRepository.findById(testTask.getId()))
                .verifyComplete();
    }

    @Test
    void findAll_ShouldReturnAllTasks() {
        // Given
        TaskEntity secondTask = TaskEntityFixtures.createTaskEntity();
        secondTask.setDescription("Second Test Task");
        secondTask = taskRepository.save(secondTask).block();

        // When
        Flux<TaskEntity> result = taskRepository.findAll();

        // Then
        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();

        // Alternative approach using blocking
        java.util.List<TaskEntity> tasks = taskRepository.findAll().collectList().block();
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    }
}
