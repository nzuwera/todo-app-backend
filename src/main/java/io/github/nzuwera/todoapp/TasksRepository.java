package io.github.nzuwera.todoapp;

import io.github.nzuwera.todoapp.entity.TaskEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface TasksRepository extends R2dbcRepository<TaskEntity, UUID> {
}
