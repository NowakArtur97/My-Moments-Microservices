#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/comment-service/src
COPY pom.xml /home/comment-service
RUN mvn -f /home/comment-service/pom.xml clean package -Dmaven.test.skip=true
#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/comment-service/target/comment-service-0.0.1-SNAPSHOT.jar /usr/local/lib/comment-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/usr/local/lib/comment-service.jar"]