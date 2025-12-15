# Stage 1: Build the JAR
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
RUN sed -i 's|dl-cdn.alpinelinux.org|dl-2.alpinelinux.org|g' /etc/apk/repositories \
 && apk update \
 && apk add --no-cache zip
RUN mkdir download
COPY scdl_bin .
COPY --from=build /app/target/*-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]