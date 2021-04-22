package com.nowakArtur97.myMoments.userService.feature.user.document;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

interface UserRepository extends MongoRepository<UserDocument, Long> {

    Optional<UserDocument> findByUsernameOrEmail(String username, String email);

    Optional<UserDocument> findByUsername(String username);

    boolean existsUserByUsername(String username);

    boolean existsUserByEmail(String email);
}
