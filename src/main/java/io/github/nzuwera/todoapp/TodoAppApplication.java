package io.github.nzuwera.todoapp;

import io.github.nzuwera.todoapp.model.Task;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
public class TodoAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoAppApplication.class, args);
    }

    /**
     * Creates a List<Task> of 1000 tasks:
     * Task#1 → "Task #1", Task#2 → "Task #2", … Task#1000 → "Task #1000"
     */
    @Bean
    public List<Task> backlog() {
        return IntStream.rangeClosed(1, 1000)
                .mapToObj(i -> new Task(
                        String.valueOf(i),
                        "Task #" + i
                ))
                .collect(Collectors.toList());
    }


}
