package io.github.nzuwera.todoapp.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


import java.time.Instant;
import java.util.UUID;

@Table(name = "tasks")
@Getter
@Setter
public class TaskEntity {
    @Id
    private UUID id;
    @Column
    private String description;
    @Column
    private boolean completed;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
