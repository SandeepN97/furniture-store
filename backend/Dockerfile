FROM gradle:8-jdk21 AS build
WORKDIR /app
COPY . /app
RUN gradle bootJar

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
