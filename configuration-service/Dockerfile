FROM openjdk:11-jdk-slim
ARG JAR_FILE=build/libs/configuration-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} configuration-service.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","configuration-service.jar"]