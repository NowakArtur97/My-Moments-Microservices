package com.nowakArtur97.myMoments.userService.feature.document;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<UserDocument, Long> {

    Optional<UserDocument> findByUsernameOrEmail(String username, String email);

    Optional<UserDocument> findByUsername(String username);

    List<UserDocument> findByUsernameIn(List<String> username);

    boolean existsUserByUsername(String username);

    boolean existsUserByEmail(String email);
}
