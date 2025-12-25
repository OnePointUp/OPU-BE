# JDK 17, lightweight alpine
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# JVM memory config
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV TZ="Asia/Seoul"

# Docker healthcheck
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Duser.timezone=${TZ:-Asia/Seoul} -jar app.jar"]