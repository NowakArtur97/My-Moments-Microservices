package com.nowakArtur97.myMoments.postService.configuration;

import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories
class MultipleMongoDatabasesConfig {

    @Primary
    @Bean(name = "postsMongoDatabaseProperties")
    @ConfigurationProperties(prefix = "my-moments.data.mongodb.posts")
    MongoProperties getPostsDatabaseProperties() {
        return new MongoProperties();
    }

    @Bean(name = "usersMongoDatabaseProperties")
    @ConfigurationProperties(prefix = "my-moments.data.mongodb.users")
    MongoProperties getUsersDatabaseProperties() {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "postsMongoTemplate")
    ReactiveMongoTemplate getPostsDatabaseMongoTemplate() {
        return new ReactiveMongoTemplate(getPostsDatabaseFactory(getPostsDatabaseProperties()));
    }

    @Bean(name = "usersMongoTemplate")
    ReactiveMongoTemplate getUsersDatabaseMongoTemplate() {
        return new ReactiveMongoTemplate(getUsersDatabaseFactory(getUsersDatabaseProperties()));
    }

    @Bean
    @Primary
    SimpleReactiveMongoDatabaseFactory getPostsDatabaseFactory(final MongoProperties mongo) {
        return new SimpleReactiveMongoDatabaseFactory(MongoClients.create(mongo.getUri()), mongo.getDatabase());
    }

    @Bean
    SimpleReactiveMongoDatabaseFactory getUsersDatabaseFactory(final MongoProperties mongo) {
        return new SimpleReactiveMongoDatabaseFactory(MongoClients.create(mongo.getUri()), mongo.getDatabase());
    }
}
