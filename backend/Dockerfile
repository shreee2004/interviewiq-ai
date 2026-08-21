# --- Build stage ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Cache Maven dependencies in their own layer before copying source
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -q -DskipTests package && \
    mv target/*.jar app.jar

# --- Runtime stage ---
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

RUN useradd --system --no-create-home appuser
COPY --from=build /app/app.jar app.jar

# Created (and owned by appuser) before the volume mount so a first-time named-volume
# mount at this path inherits the right ownership — see docker/docker-compose.yml.
RUN mkdir -p /app/uploads/resumes && chown -R appuser /app/uploads
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
