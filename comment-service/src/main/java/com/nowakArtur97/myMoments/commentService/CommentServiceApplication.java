package com.nowakArtur97.myMoments.commentService;

import com.nowakArtur97.myMoments.commentService.feature.messaging.PostEventStream;
import com.nowakArtur97.myMoments.commentService.feature.messaging.UserEventStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableEurekaClient
@EnableReactiveMongoAuditing
@EnableBinding({UserEventStream.class, PostEventStream.class})
public class CommentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommentServiceApplication.class, args);
    }

}
