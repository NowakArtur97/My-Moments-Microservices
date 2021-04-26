package com.nowakArtur97.myMoments.postService.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages =
        "com.nowakArtur97.myMoments.postService.feature.post.document",
        mongoTemplateRef = "postsMongoTemplate")
class PostsDatabaseMongoConfiguration {
}
