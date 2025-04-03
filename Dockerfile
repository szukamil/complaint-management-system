FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests

FROM openjdk:17-slim
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]
