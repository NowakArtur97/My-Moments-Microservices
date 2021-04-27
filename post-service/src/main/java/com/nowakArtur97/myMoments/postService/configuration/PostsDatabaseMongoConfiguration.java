package com.nowakArtur97.myMoments.postService.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages =
        "com.nowakArtur97.myMoments.postService.feature.post.document",
        reactiveMongoTemplateRef = "postsMongoTemplate")
class PostsDatabaseMongoConfiguration {
}
