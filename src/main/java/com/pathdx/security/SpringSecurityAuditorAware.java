package com.pathdx.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(new String(" "));
        }
        if (authentication.getPrincipal() instanceof LoggedInUserDetail) {
            LoggedInUserDetail loggedInUserDetail = (LoggedInUserDetail) authentication.getPrincipal();
            return Optional.of(loggedInUserDetail.getUsername()+ " - "+ loggedInUserDetail.getName());
        } else
            return Optional.of(new String(" "));
    }
}
