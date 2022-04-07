FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package

FROM openjdk:17
COPY --from=builder /app/target/kafka-microframeworks-javalin-*dependencies.jar app.jar
EXPOSE 8080
CMD ["java", "-Xmx1024m", "-jar", "app.jar"]
