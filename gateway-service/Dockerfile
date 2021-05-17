FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/gateway-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} gateway-service.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","gateway-service.jar"]