package com.abranlezama.awstodoapplication.config;

import com.amazonaws.regions.AwsRegionProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
public class AwsConfig {

    /*
    In local development we don't want to connect to cognito.
    This bean will not be activated if our custom property
    user-cognito-as-identity-provider is set to false
     */

    @Bean
    @ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
    public CognitoIdentityProviderClient cognitoIdentityProviderClient(
            AwsRegionProvider regionProvider,
            AwsCredentialsProvider awsCredentialsProvider) {
        return CognitoIdentityProviderClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.of(regionProvider.getRegion()))
                .build();
    }
}
