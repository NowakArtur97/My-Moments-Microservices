package com.nowakArtur97.myMoments.userService.eventListener;

import com.nowakArtur97.myMoments.userService.feature.user.document.RoleDocument;
import com.nowakArtur97.myMoments.userService.feature.user.document.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class OnApplicationStartupEventListener {

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    @Autowired
    private final RoleRepository roleRepository;

    @EventListener
    void onApplicationStartup(ContextRefreshedEvent event) {

        if (roleRepository.findByName(defaultUserRole).isEmpty()) {
            roleRepository.save(new RoleDocument(defaultUserRole));
        }
    }
}
