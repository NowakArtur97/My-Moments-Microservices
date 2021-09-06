package com.nowakArtur97.myMoments.followerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;
import org.springframework.data.neo4j.config.EnableReactiveNeo4jAuditing;

@SpringBootApplication
@EnableReactiveNeo4jAuditing
public class FollowerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FollowerServiceApplication.class, args);
    }
}
