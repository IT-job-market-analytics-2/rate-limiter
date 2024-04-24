#
# Build stage
#
FROM maven:3.9.4-eclipse-temurin-17-alpine AS build
WORKDIR /rate-limiter
COPY pom.xml .
RUN mvn verify
COPY . .
RUN ["mvn", "package", "-Dmaven.test.skip=true"]

#
# Package stage
#
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /rate-limiter
COPY --from=build /rate-limiter/target/*.jar rate-limiter.jar
ENTRYPOINT ["java", "-jar", "rate-limiter.jar" ]