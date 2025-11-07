const fs = require('fs');
const path = require('path');

const subPath = process.argv[2];  // e.g., order
const className = process.argv[3]; // e.g., Order

if (!subPath || !className) {
    console.error('사용법: node generate.js <subPath> <ClassName>');
    process.exit(1);
}

const baseDir = path.join('src/main/java/com/opu/opube/feature', subPath);
const packageNameBase = `com.opu.opube.feature.${subPath}`;

const structure = [
    // ---------- COMMAND ----------
    { dir: `command/application/controller`, file: `${className}CommandController.java` },
    { dir: `command/application/dto/request` },
    { dir: `command/application/dto/response` },
    { dir: `command/application/service`, file: `${className}CommandService.java` },
    { dir: `command/application/service`, file: `${className}CommandServiceImpl.java` },

    { dir: `command/domain/aggregate`, file: `${className}.java` },
    { dir: `command/domain/repository`, file: `${className}Repository.java` },
    { dir: `command/infrastructure/repository`, file: `Jpa${className}Repository.java` },

    // ---------- QUERY ----------
    { dir: `query/controller`, file: `${className}QueryController.java` },
    { dir: `query/dto/request` },
    { dir: `query/dto/response` },
    { dir: `query/service`, file: `${className}QueryService.java` },
    { dir: `query/infrastructure/repository`, file: `${className}QueryRepository.java` },
    { dir: `query/infrastructure/repository`, file: `${className}QueryRepositoryImpl.java` },
];

function createFile(dirPath, fileName, content) {
    const filePath = path.join(dirPath, fileName);
    fs.writeFileSync(filePath, content);
    console.log(`✅ File created: ${filePath}`);
}

structure.forEach(item => {
    const targetDir = path.join(baseDir, item.dir);
    if (!fs.existsSync(targetDir)) {
        fs.mkdirSync(targetDir, { recursive: true });
        console.log(`📁 Directory: ${targetDir}`);
    }

    if (!item.file) return;

    const packageName = `${packageNameBase}.${item.dir.replace(/\//g, '.')}`;

    let content = '';

    // COMMAND CONTROLLER
    if (item.file === `${className}CommandController.java`) {
        content = `
package ${packageName};

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ${packageNameBase}.command.application.service.${className}CommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/${subPath}")
public class ${className}CommandController {

    private final ${className}CommandService ${className.charAt(0).toLowerCase() + className.slice(1)}CommandService;

}
`.trim();
    }

    // COMMAND SERVICE INTERFACE
    else if (item.file === `${className}CommandService.java`) {
        content = `
package ${packageName};

public interface ${className}CommandService {
    // command methods here
}
`.trim();
    }

    // COMMAND SERVICE IMPL
    else if (item.file === `${className}CommandServiceImpl.java`) {
        content = `
package ${packageName};

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${packageNameBase}.command.domain.repository.${className}Repository;

@Service
@RequiredArgsConstructor
@Transactional
public class ${className}CommandServiceImpl implements ${className}CommandService {

    private final ${className}Repository ${className.charAt(0).toLowerCase() + className.slice(1)}Repository;

}
`.trim();
    }

    // ENTITY
    else if (item.file === `${className}.java`) {
        content = `
package ${packageName};

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ${className} {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
}
`.trim();
    }

    // DOMAIN REPOSITORY INTERFACE
    else if (item.file === `${className}Repository.java`) {
        content = `
package ${packageName};

import java.util.UUID;
import ${packageNameBase}.command.domain.aggregate.${className};

public interface ${className}Repository {
    ${className} save(${className} ${className.toLowerCase()});
}
`.trim();
    }

    // JPA IMPLEMENTATION
    else if (item.file === `Jpa${className}Repository.java`) {
        content = `
package ${packageName};

import org.springframework.data.jpa.repository.JpaRepository;
import ${packageNameBase}.command.domain.aggregate.${className};
import ${packageNameBase}.command.domain.repository.${className}Repository;

public interface Jpa${className}Repository extends ${className}Repository, JpaRepository<${className}, Long> {
}
`.trim();
    }

    // QUERY CONTROLLER
    else if (item.file === `${className}QueryController.java`) {
        content = `
package ${packageName};

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ${packageNameBase}.query.service.${className}QueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/${subPath}")
public class ${className}QueryController {

    private final ${className}QueryService ${className.charAt(0).toLowerCase() + className.slice(1)}QueryService;

}
`.trim();
    }

    // QUERY SERVICE
    else if (item.file === `${className}QueryService.java`) {
        content = `
package ${packageName};

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${packageNameBase}.query.infrastructure.repository.${className}QueryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ${className}QueryService {

    private final ${className}QueryRepository ${className.charAt(0).toLowerCase() + className.slice(1)}QueryRepository;

}
`.trim();
    }

    // QUERY REPOSITORY (인터페이스)
    else if (item.file === `${className}QueryRepository.java`) {
        content = `
package ${packageName};

import java.util.List;
import ${packageNameBase}.command.domain.aggregate.${className};

public interface ${className}QueryRepository {
    List<${className}> findAll();
}
`.trim();
    }

    // QUERY REPOSITORY IMPLEMENTATION (QueryDSL)
    else if (item.file === `${className}QueryRepositoryImpl.java`) {
        content = `
package ${packageName};

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ${packageNameBase}.command.domain.aggregate.${className};
import ${packageNameBase}.command.domain.aggregate.Q${className};
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ${className}QueryRepositoryImpl implements ${className}QueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<${className}> findAll() {
        Q${className} ${className.toLowerCase()} = Q${className}.${className.toLowerCase()};
        return queryFactory
                .selectFrom(${className.toLowerCase()})
                .fetch();
    }
}
`.trim();
    }

    createFile(targetDir, item.file, content);
});
