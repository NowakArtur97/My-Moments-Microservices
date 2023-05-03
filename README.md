# MyMoments

## Table of Contents

- [General info](#general-info)
- [Setup](#setup)
- [Built With](#built-with)
- [Features](#features)
- [To Do](#to-do)
- [Endpoints List](#endpoints-list)
- [Status](#status)

## General info

A microservice application created while writing a thesis entitled: "Functionality problems of monolithic architecture and microservices in web applications". Version in monolith architecture: https://github.com/NowakArtur97/My-Moments-Monolith.

## Setup

To start the application, in the folder, enter the following commands in command line:

- `docker-compose up -d`
- `docker-compose down`

Use the login details provided above to generate the token or create new account by sending the appropriate request:

```json
# POST /api/v1/registration
# Content-Type: application/json
{
    "username" : "user123",
    "password" : "Password1@",
    "matchingPassword" : "Password1@",
    "email" : "email@something.com"
}
```

The password must meet the following requirements:

- Must be between 6 and 30 characters long
- Passwords must match
- Mustn't contain the username
- Mustn't contain spaces
- Mustn't contain a repetitive string of characters longer than 3 characters
- Mustn't be on the list of popular passwords
- Must contain 1 or more uppercase characters
- Must contain 1 or more lowercase characters
- Must contain 1 or more special characters

Then generate JWT. The token can be generated using a username or email address. Password is required.

```json
# POST /api/v1/authentication
# Content-Type: application/json
{
  "username": "user123",
  "password" : "Password1@"
}
```

Then use the token as a Bearer Token using e.g. Postman. Requests can be sent to the API Gateway address: `http://YOUR_DOCKER_IP_OR_LOCALHOST:8989`.

## Built With

- Java 11
- Spring (Boot, MVC, Security, Data MongoDB, Webflux, Data MongoDB Reactive, Data Neo4j, Devtools, Actuator, Retry, Cloud (Eureka Client, Eureka Server, Config Server, Gateway, Resilience4j, Stream Binder Rabbit, Sleuth, Zipkin, Bootstrap)) - 2.4.5
- Swagger (Core, Bean Validators, UI) - 2.92
- Lombok - 1.18.16
- jUnit5 - 5.7.2
- Mockito - 3.8.0
- Model Mapper - 2.4.0
- JSON Web Token Support For The JVM (jjwt) - 0.9.1
- Passay - 1.6.0
- Gradle
- Maven
- Docker
- RabbitMQ
- Sleuth
- Zipkin
- Logback
- MongoDB
- Neo4j
- Vault
- Elasticsearch
- Kibana
- Logstash (logstash-logback-encoder) - 6.6

## Features

- Service discovery
- API Gateway
- Configuration server
- Centralized logging with ELK stack (Elasticsearch - Kibana - Logstash)
- Distributed tracing with Sleuth and Zipkin
- Sending messages using RabbitMQ
- Circuit breaker
- Data hidden in Vault
- Automatically add secrets to the Vault
- User registration
- JWT authorization
- Users API
- Posts API
- Comments API
- Followers API
- Documentation created using Swagger 2
- Custom password validation
- User recommendations using Neo4j

## To Do

- Adding fake data at specified intervals

## Endpoints List:

### Security

| Method | URI                      | Action                                |
| ------ | ------------------------ | ------------------------------------- |
| `POST` | `/api/v1/registration`   | `Create an account`    |
| `POST` | `/api/v1/authentication` | `Generate a JWT`                        |

### Users

| Method    | URI                          | Action                                                               |
| --------- | ---------------------------- | -------------------------------------------------------------------- |
| `GET`     | `/api/v1/users/photos?usernames=${username1,username2}`        | `Get users photos by usernames`      
| `PUT`     | `/api/v1/users/me`        | `Update user information`                                      |
| `DELETE`     | `/api/v1/users/me`        | `Delete user`                                      |

### Posts

| Method    | URI                          | Action                                                               |
| --------- | ---------------------------- | -------------------------------------------------------------------- |
| `GET`     | `/api/v1/posts/{id}` | `Get information about a post`                     |
| `GET`     | `/api/v1/posts/me` | `Get user's posts`                     |
| `GET`     | `/api/v1/posts?usernames=${username1,username2}&page=${page}&size=${sizeOfPage}&sort=${sortingConditions},${asc|desc}`        | `Get users posts by usernames (with optional paging)`
| `POST`     | `/api/v1/posts`        | `Create a post`
| `PUT`     | `/api/v1/posts/{id}`        | `Update post information`                                      |
| `DELETE`     | `/api/v1/posts/{id}`        | `Delete post with related comments`                                      |

### Comments

| Method    | URI                          | Action                                                               |
| --------- | ---------------------------- | -------------------------------------------------------------------- |
| `GET`     | `/api/v1/posts/{postId}/comments` | `Get the post's comments`                     |
| `POST`     | `/api/v1/posts/{postId}/comments`        | `Add a comment to the post`
| `PUT`     | `/api/v1/posts/{postId}/comments/{commentId}`        | `Update the comment content`                                      |
| `DELETE`     | `/api/v1/posts/{postId}/comments/{commentId}`        | `Delete comment`                                      |

### Followers

| Method  | URI                                            | Action                                                               |
|---------|------------------------------------------------| -------------------------------------------------------------------- |
| `GET`   | `/api/v1/followers/{username}`                 | `Get user's followers by username`
| `GET`   | `/api/v1/following/recommendations/{username}` | `Get user's following recommendations by username`
| `GET`   | `/api/v1/following/{username}`                 | `Get user's following by username`
| `POST`  | `/api/v1/following/{username}`                 | `Follow the User`
| `Delete` | `/api/v1/following/{username}`                 | `Unollow the User`

## Status

Project is: in progress
