#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/discovery-service/src
COPY pom.xml /home/discovery-service
RUN mvn -f /home/discovery-service/pom.xml clean package -Dmaven.test.skip=true
#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/discovery-service/target/discovery-service-0.0.1-SNAPSHOT.jar /usr/local/lib/discovery-service.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/usr/local/lib/discovery-service.jar"]
