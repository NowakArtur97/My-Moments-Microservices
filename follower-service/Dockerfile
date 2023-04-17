#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/follower-service/src
COPY pom.xml /home/follower-service
RUN mvn -f /home/follower-service/pom.xml clean package -Dmaven.test.skip=true
#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/follower-service/target/follower-service-0.0.1-SNAPSHOT.jar /usr/local/lib/follower-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/usr/local/lib/follower-service.jar"]