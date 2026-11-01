# -----------------------------------------------------------------------------
# Abha Pharmacy - Production Multi-Stage Docker Build
# Target Architecture: Eclipse Temurin JRE 21 Alpine (Ultra-lightweight & Secure)
# -----------------------------------------------------------------------------

# Stage 1: Build & Package Artifact
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Pre-cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source and compile production JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal Production Runtime
FROM eclipse-temurin:21-jre-alpine AS runtime

LABEL maintainer="Haras Kumar <https://github.com/Haraskumar>"
LABEL description="Abha Clinical Pharmacy & Dispensary Enterprise Management Portal"

# Security Hardening: Run as unprivileged non-root user
RUN addgroup -S abhagroup && adduser -S abhauser -G abhagroup
WORKDIR /app

# Copy executable JAR from builder stage
COPY --from=builder /build/target/Abha_Pharmacy-0.0.1-SNAPSHOT.jar /app/app.jar

# Setup prescription storage directory with correct permissions
RUN mkdir -p /app/app_data/prescriptions && chown -R abhauser:abhagroup /app

USER abhauser

# Expose standard application port
EXPOSE 8081
ENV PORT=8081
ENV SPRING_PROFILES_ACTIVE=production

# JVM Container Optimization
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "/app/app.jar"]
