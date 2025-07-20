package io.github.nzuwera.todoapp.service;

import io.github.nzuwera.todoapp.TasksRepository;
import io.github.nzuwera.todoapp.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final TasksRepository tasksRepository;

    @Override
    public Flux<Task> getTasks() {
        return tasksRepository.findAll().map(taskEntity -> {
            Task task = new Task();
            task.setId(taskEntity.getId().toString());
            task.setDescription(taskEntity.getDescription());
            task.setCompleted(taskEntity.isCompleted());
            task.setCreatedAt(taskEntity.getCreatedAt().toString());
            task.setUpdatedAt(taskEntity.getUpdatedAt().toString());
            return task;
        });
    }

}

