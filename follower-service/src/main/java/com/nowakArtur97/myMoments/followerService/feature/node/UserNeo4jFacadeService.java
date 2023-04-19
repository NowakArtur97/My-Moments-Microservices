package com.nowakArtur97.myMoments.followerService.feature.node;

import com.nowakArtur97.myMoments.followerService.feature.resource.UserModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Record;
import org.springframework.data.neo4j.core.ReactiveNeo4jClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNeo4jFacadeService {

    private final static String USERNAME_VARIABLE = "username";
    private final static String FOLLOWING_VARIABLE_NAME = "following";
    private final static String FOLLOWERS_VARIABLE_NAME = "followers";

    private final ReactiveNeo4jClient reactiveClient;

    public Flux<UserModel> runFindUsersQuery(String username, String query) {
        return reactiveClient.query(query)
                .bind(username).to(USERNAME_VARIABLE)
                .fetchAs(UserModel.class)
                .mappedBy((ts, record) -> mapToUserModel(record))
                .all();
    }

    private UserModel mapToUserModel(Record record) {
        return new UserModel(
                record.get(USERNAME_VARIABLE).asString(),
                record.get(FOLLOWING_VARIABLE_NAME).asInt(),
                record.get(FOLLOWERS_VARIABLE_NAME).asInt());
    }
}
