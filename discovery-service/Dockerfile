FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/discovery-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} discovery-service.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","discovery-service.jar"]