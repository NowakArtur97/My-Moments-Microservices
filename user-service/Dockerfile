#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/user-service/src
COPY pom.xml /home/user-service
RUN mvn -f /home/user-service/pom.xml clean package -Dmaven.test.skip=true
#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/user-service/target/user-service-0.0.1-SNAPSHOT.jar /usr/local/lib/user-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/usr/local/lib/user-service.jar"]