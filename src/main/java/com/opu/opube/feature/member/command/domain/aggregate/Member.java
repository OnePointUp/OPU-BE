package com.opu.opube.feature.member.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
}