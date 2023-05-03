package com.abranlezama.awstodoapplication.registration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;

@Service
@ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")

public class CognitoRegistrationService implements RegistrationService{

   private final CognitoIdentityProviderClient cognitoIdentityProviderClient;
    private final String userPoolId;

    public CognitoRegistrationService(
            @Value("${COGNITO_USER_POOL_ID}") String userPoolId,
            CognitoIdentityProviderClient cognitoIdentityProviderClient) {
        this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
        this.userPoolId = userPoolId;
    }

    @Override
    public void registerUser(Registration registration) {
        AdminCreateUserRequest registrationRequest = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(registration.getUsername())
                .userAttributes(
                        AttributeType.builder().name("email").value(registration.getEmail()).build(),
                        AttributeType.builder().name("name").value(registration.getUsername()).build(),
                        AttributeType.builder().name("email_verified").value("true").build()
                )
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .forceAliasCreation(Boolean.FALSE)
                .build();

        cognitoIdentityProviderClient.adminCreateUser(registrationRequest);
    }
}
