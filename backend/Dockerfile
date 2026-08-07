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
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
