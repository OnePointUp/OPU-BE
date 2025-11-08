package com.opu.opube.feature.todo.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
}