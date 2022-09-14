#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/post-service/src
COPY pom.xml /home/post-service
RUN mvn -f /home/post-service/pom.xml clean package -Dmaven.test.skip=true
#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/post-service/target/post-service-0.0.1-SNAPSHOT.jar /usr/local/lib/post-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/usr/local/lib/post-service.jar"]