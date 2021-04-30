package com.nowakArtur97.myMoments.postService.configuration.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages =
        "com.nowakArtur97.myMoments.postService.feature.post",
        reactiveMongoTemplateRef = "postsMongoTemplate")
class PostsDatabaseMongoConfiguration {
}
