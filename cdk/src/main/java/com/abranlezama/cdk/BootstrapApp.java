package com.abranlezama.cdk;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class BootstrapApp {
    public static void main(String[] args) {
        App app = new App();

        String regin = (String) app.getNode().tryGetContext("region");

        String accountId = (String) app.getNode().tryGetContext("accountId");

        Environment awsEnvironment = makeEnv(accountId, regin);

        new Stack(app, "Bootstrap", StackProps.builder()
                .env(awsEnvironment)
                .build());

        app.synth();
    }

    static Environment makeEnv(String account, String region) {
        return Environment.builder()
                .account(account)
                .region(region)
                .build();
    }
}
