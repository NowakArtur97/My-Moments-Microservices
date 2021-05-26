package com.nowakArtur97.myMoments.userService.feature.document;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String value) throws UsernameNotFoundException {

        log.info("Looking up User with name/email: {}", value);

        UserDocument userDocument = userRepository.findByUsernameOrEmail(value, value)
                .orElseThrow(() -> new UsernameNotFoundException("User with name/email: '" + value + "' not found."));

        return new User(userDocument.getUsername(), userDocument.getPassword(), getAuthorities(userDocument.getRoles()));
    }

    public List<GrantedAuthority> getAuthorities(Set<RoleDocument> roles) {

        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
