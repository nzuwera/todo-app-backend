package io.github.nzuwera.todoapp.service;

import io.github.nzuwera.todoapp.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final List<Task> backlog;

    /**
     * Emits one Task every second, then completes.
     */
    public Flux<Task> streamAllTasks() {
        return Flux
                .fromIterable(backlog)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(task -> task)
                .sequential()
                .delayElements(Duration.ofSeconds(1));
    }


    /**
     * Returns a Flux<Task> for the given page and size.
     */
    public Flux<Task> findTasks(int page, int size) {
        return Flux.fromIterable(backlog)
                .skip((long) page * size)
                .take(size);
    }
}

