package com.abranlezama.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import dev.stratospheric.cdk.Network;
import dev.stratospheric.cdk.Service;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import java.util.HashMap;
import java.util.Map;

public class ServiceApp {
    public static void main(String[] args) {
        App app = new App();

        String environmentName = (String) app.getNode()
                .getContext("environmentName");

        String applicationName = (String) app.getNode()
                .tryGetContext("applicationName");

        String accountId = (String) app.getNode()
                .tryGetContext("accountId");

        String springProfile = (String) app.getNode()
                .tryGetContext("springProfile");

        String dockerImageUrl = (String) app.getNode()
                .tryGetContext("dockerImageUrl");

        String region = (String) app.getNode()
                .tryGetContext("region");

        Environment awsEnvironment = makeEnv(accountId, region);

        ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
                applicationName,
                environmentName
        );

        Stack serviceStack = new Stack(app, "ServiceStack", StackProps.builder()
                .stackName(applicationEnvironment.prefix("Service"))
                .env(awsEnvironment)
                .build());

        Service.DockerImageSource dockerImageSource = new Service.DockerImageSource(dockerImageUrl);
        Network.NetworkOutputParameters networkOutputParameters = Network
                .getOutputParametersFromParameterStore(serviceStack, applicationEnvironment.getEnvironmentName());
        Service.ServiceInputParameters serviceInputParameters = new Service
                .ServiceInputParameters(dockerImageSource, environmentVariable(springProfile))
                .withHealthCheckIntervalSeconds(30);

        Service service = new Service(
                serviceStack,
                "Service",
                awsEnvironment,
                applicationEnvironment,
                serviceInputParameters,
                networkOutputParameters
        );

        app.synth();
    }

    static Map<String, String> environmentVariable(String springProfile) {
        Map<String, String> vars = new HashMap<>();
        vars.put("SPRING_PROFILES_ACTIVE", springProfile);
        return vars;
    }

    static Environment makeEnv(String account, String region) {
        return Environment.builder()
                .account(account)
                .region(region)
                .build();
    }
}
