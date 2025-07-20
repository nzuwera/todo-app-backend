package io.github.nzuwera.todoapp.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private String id;
    @NotEmpty(message = "Task description cannot be empty")
    private String description;
    private boolean completed = false;
    private String createdAt = Instant.now().toString();
    private String updatedAt = Instant.now().toString();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return completed == task.completed &&
                Objects.equals(id, task.id) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, completed);
    }
}
