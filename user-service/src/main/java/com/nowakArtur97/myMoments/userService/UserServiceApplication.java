package com.nowakArtur97.myMoments.userService;

import com.nowakArtur97.myMoments.userService.feature.messaging.PostEventStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableEurekaClient
@EnableMongoAuditing
@EnableBinding(PostEventStream.class)
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
