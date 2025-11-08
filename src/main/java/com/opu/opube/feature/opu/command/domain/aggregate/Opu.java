package com.opu.opube.feature.opu.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Opu {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
}