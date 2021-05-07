FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/post-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} post-service.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","post-service.jar"]