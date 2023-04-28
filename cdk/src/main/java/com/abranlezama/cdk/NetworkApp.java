package com.abranlezama.cdk;

import dev.stratospheric.cdk.Network;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class NetworkApp {
    public static void main(String[] args) {
        App app = new App();

        String environmentName = (String) app.getNode().tryGetContext("environmentName");

        String accountId = (String) app.getNode().tryGetContext("accountId");

        String region = (String) app.getNode().tryGetContext("region");

        Environment awsEnvironment = makeEnv(accountId, region);

        Stack networkStack = new Stack(
                app,
                "NetworkStack",
                StackProps.builder()
                        .stackName(environmentName + "-Network")
                        .env(awsEnvironment)
                        .build()
        );

        new Network(
                networkStack,
                "Network",
                awsEnvironment,
                environmentName,
                new Network.NetworkInputParameters()
        );

        app.synth();
    }

    static Environment makeEnv(String account, String region) {
        return Environment.builder()
                .account(account)
                .region(region)
                .build();
    }
}
