// src/testFixtures/java/io/github/nzuwera/todoapp/fixtures/TaskFixtures.java
package io.github.nzuwera.todoapp;

import io.github.nzuwera.todoapp.model.Task;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TaskFixtures {

    public static Task createTask() {
        String timestamp = Instant.now()
                .atZone(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT);

        return new Task(
                UUID.randomUUID().toString(),
                "Test Task",
                false,
                timestamp,
                timestamp
        );
    }
}