FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/user-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} user-service.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","user-service.jar"]