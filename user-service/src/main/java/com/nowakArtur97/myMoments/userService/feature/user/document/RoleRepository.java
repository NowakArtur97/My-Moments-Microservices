package com.nowakArtur97.myMoments.userService.feature.user.document;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<RoleDocument, Long> {

    Optional<RoleDocument> findByName(String name);
}
