# Stage 1: Build with Maven
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Install wget to download Maven if needed
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Download and use Maven (instead of Maven wrapper)
RUN wget -q https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz && \
    tar -xzf apache-maven-3.9.6-bin.tar.gz && \
    rm apache-maven-3.9.6-bin.tar.gz && \
    ln -s /app/apache-maven-3.9.6/bin/mvn /usr/local/bin/mvn

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/WellConnect-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 10000

# Environment variables
ENV SERVER_PORT=10000
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
