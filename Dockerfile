# ==========================================================
# Stage 1: Build stage (Maven + JDK 21)
# ==========================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# 1. Copy POM and download dependencies (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copy source code and build the executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================================
# Stage 2: Runtime stage (Lightweight JRE 21)
# ==========================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Run as non-root user for container security
RUN addgroup -S gymgroup && adduser -S gymuser -G gymgroup
USER gymuser

# Copy only the compiled JAR artifact from builder stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

