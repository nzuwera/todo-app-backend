package io.github.nzuwera.todoapp.service;

import io.github.nzuwera.todoapp.repository.TaskRepository;
import io.github.nzuwera.todoapp.entity.TaskEntity;
import io.github.nzuwera.todoapp.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;

    private static Task mapToTask(TaskEntity entity) {
        Task newTask = new Task();
        newTask.setId(entity.getId().toString());
        newTask.setDescription(entity.getDescription());
        newTask.setCompleted(entity.isCompleted());
        newTask.setCreatedAt(entity.getCreatedAt().toString());
        newTask.setUpdatedAt(entity.getUpdatedAt().toString());
        return newTask;
    }

    @Override
    public Flux<Task> getTasks() {
        return taskRepository.findAll().map(TaskService::mapToTask);
    }

    @Override
    public Mono<Task> createTask(Task task) {
        return checkTaskExists(task.getDescription())
                .then(Mono.defer(() -> saveNewTask(task)));
    }

    private Mono<Void> checkTaskExists(String description) {
        return taskRepository.findByDescription(description)
                .flatMap(existingTask -> Mono.error(
                        new IllegalArgumentException("Task with this description already exists")
                ))
                .then();
    }

    private Mono<Task> saveNewTask(Task task) {
        TaskEntity taskEntity = mapToTaskEntity(task);
        return taskRepository.save(taskEntity)
                .map(TaskService::mapToTask);
    }

    private static TaskEntity mapToTaskEntity(Task task) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setDescription(task.getDescription());
        taskEntity.setCompleted(task.isCompleted());
        taskEntity.setUpdatedAt(Instant.parse(task.getUpdatedAt()));
        taskEntity.setCreatedAt(Instant.parse(task.getCreatedAt()));
        return taskEntity;
    }
}

