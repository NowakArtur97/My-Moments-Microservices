#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/configuration-service/src
COPY pom.xml /home/configuration-service
RUN mvn -f /home/configuration-service/pom.xml clean package -Dmaven.test.skip=true
#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/configuration-service/target/configuration-service-0.0.1-SNAPSHOT.jar /usr/local/lib/configuration-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/usr/local/lib/configuration-service.jar"]