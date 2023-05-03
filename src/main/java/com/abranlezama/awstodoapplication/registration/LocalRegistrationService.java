package com.abranlezama.awstodoapplication.registration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "false")
public class LocalRegistrationService implements RegistrationService {
    @Override
    public void registerUser(Registration registration) {
        // no registration as we use local Keycloak instance with a pre-defined set of users
    }
}
