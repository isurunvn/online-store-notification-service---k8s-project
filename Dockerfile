FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean verify -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8084
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -q -O - http://localhost:8084/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=60.0", "-jar", "app.jar"]
