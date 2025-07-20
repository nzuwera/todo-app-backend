// src/testFixtures/java/io/github/nzuwera/todoapp/fixtures/TaskFixtures.java
package io.github.nzuwera.todoapp;

import io.github.nzuwera.todoapp.entity.TaskEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class TaskEntityFixtures {

    public static TaskEntity createTaskEntity() {
        TaskEntity entity = new TaskEntity();
        entity.setId(UUID.randomUUID());
        entity.setDescription("Test Task");
        entity.setCompleted(false);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }
}