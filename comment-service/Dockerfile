FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/comment-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} comment-service.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","comment-service.jar"]