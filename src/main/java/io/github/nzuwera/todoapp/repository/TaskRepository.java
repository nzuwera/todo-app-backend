package io.github.nzuwera.todoapp.repository;

import io.github.nzuwera.todoapp.entity.TaskEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface TaskRepository extends R2dbcRepository<TaskEntity, UUID> {
    Mono<TaskEntity> findByDescription(String description);
}
