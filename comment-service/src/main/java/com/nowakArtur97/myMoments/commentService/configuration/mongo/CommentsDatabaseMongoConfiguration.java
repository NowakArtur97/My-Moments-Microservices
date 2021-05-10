package com.nowakArtur97.myMoments.commentService.configuration.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages =
        "com.nowakArtur97.myMoments.commentService.feature.comment",
        reactiveMongoTemplateRef = "commentsMongoTemplate")
class CommentsDatabaseMongoConfiguration {
}
