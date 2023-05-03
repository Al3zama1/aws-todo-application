package com.abranlezama.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;

public class CognitoApp {
    public static void main(String[] args) {
        App app = new App();

        String environmentName = (String) app.getNode()
                .tryGetContext("environmentName");

        String applicationName = (String) app.getNode()
                .tryGetContext("applicationName");

        String accountId = (String) app.getNode()
                .tryGetContext("accountId");

        String region = (String) app.getNode()
                .tryGetContext("region");

        String applicationUrl = (String) app.getNode()
                .tryGetContext("applicationUrl");

        String loginPageDomainPrefix = (String) app.getNode()
                .tryGetContext("loginPageDomainPrefix");

        Environment awsEnvironment = makeEnv(accountId, region);

        ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
                applicationName,
                environmentName
        );

        new CognitoStack(app,
                "congito",
                awsEnvironment,
                applicationEnvironment,
                new CognitoStack.CognitoInputParameters(
                    applicationName,
                    applicationUrl,
                    loginPageDomainPrefix
        ));

        app.synth();
    }

    static Environment makeEnv(String account, String region) {
        return Environment.builder()
                .account(account)
                .region(region)
                .build();
    }
}
