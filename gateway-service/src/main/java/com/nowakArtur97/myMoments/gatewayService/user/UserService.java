package com.nowakArtur97.myMoments.gatewayService.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<UserDocument> findByUsernameOrEmail(String usernameOrEmail) {

        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }
}
