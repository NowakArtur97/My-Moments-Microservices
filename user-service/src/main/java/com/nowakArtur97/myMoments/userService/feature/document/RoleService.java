package com.nowakArtur97.myMoments.userService.feature.document;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class RoleService {

    private final RoleRepository roleRepository;

    public Optional<RoleDocument> findByName(String name) {

        return roleRepository.findByName(name);
    }
}
