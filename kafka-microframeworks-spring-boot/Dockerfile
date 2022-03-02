FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package

FROM openjdk:17-jdk-alpine
COPY --from=builder /app/target/kafka-microframeworks-spring-boot-*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]