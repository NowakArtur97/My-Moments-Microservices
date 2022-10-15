#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/gateway-service/src
COPY pom.xml /home/gateway-service
RUN mvn -f /home/gateway-service/pom.xml clean package -Dmaven.test.skip=true
#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar /usr/local/lib/gateway-service.jar
EXPOSE 8989
ENTRYPOINT ["java", "-jar", "/usr/local/lib/gateway-service.jar"]
