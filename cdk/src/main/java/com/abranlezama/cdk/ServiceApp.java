package com.abranlezama.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import dev.stratospheric.cdk.Network;
import dev.stratospheric.cdk.Service;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abranlezama.cdk.Validations.requireNonEmpty;

public class ServiceApp {
    public static void main(String[] args) {
        App app = new App();

        String environmentName = (String) app.getNode()
                .tryGetContext("environmentName");
        requireNonEmpty(environmentName, "context variable 'environmentName' must not be null");

        String applicationName = (String) app.getNode()
                .tryGetContext("applicationName");
        requireNonEmpty(applicationName, "context variable 'applicationName' must not be null");

        String accountId = (String) app.getNode()
                .tryGetContext("accountId");
        requireNonEmpty(accountId, "context variable 'accountId mut not be null");

        String springProfile = (String) app.getNode()
                .tryGetContext("springProfile");
        requireNonEmpty(springProfile, "context variable 'springProfile must not be null'");

        String dockerImageUrl = (String) app.getNode()
                .tryGetContext("dockerImageUrl");
        requireNonEmpty(dockerImageUrl, "context variable 'dockerImageUrl' must not be null");

        String region = (String) app.getNode()
                .tryGetContext("region");
        requireNonEmpty(region, "context variable 'region' must not be null");

        Environment awsEnvironment = makeEnv(accountId, region);

        ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
                applicationName,
                environmentName
        );

        long timestamp = System.currentTimeMillis();
        Stack parametersStack = new Stack(app, "ServiceParameters-" + timestamp, StackProps.builder()
                .stackName(applicationEnvironment.prefix("Service-Parameters-" + timestamp))
                .env(awsEnvironment)
                .build());

        CognitoStack.CognitoOutputParameters cognitoOutputParameters =
                CognitoStack.getOutputParametersFromParameterStore(parametersStack, applicationEnvironment);

        Stack serviceStack = new Stack(app, "ServiceStack", StackProps.builder()
                .stackName(applicationEnvironment.prefix("Service"))
                .env(awsEnvironment)
                .build());

        Service.DockerImageSource dockerImageSource = new Service.DockerImageSource(dockerImageUrl);
        Network.NetworkOutputParameters networkOutputParameters = Network.getOutputParametersFromParameterStore(serviceStack, applicationEnvironment.getEnvironmentName());
        Service.ServiceInputParameters serviceInputParameters = new Service.ServiceInputParameters(dockerImageSource,Collections.emptyList(), environmentVariables(springProfile, cognitoOutputParameters))
                /*
                Sticky session is used to ensure that users are always directed to the same app instance they first reached.
                When an authorization code flow is started, the spring boot instance generates a state property to protect against CSRF
                After the authentication is successful, users must be redirected to the same app instance that initiated the process
                with the authorization code and the state property that must match the one stored in the app instance.

                A sample OAuth 2.0 Authorization Code Grant request looks like this:
                https://authorization-server.com/oauth/authorize
                  ?client_id=a17c21ed
                  &response_type=code
                  &state=5ca75bd30
                  &redirect_uri=https%3A%2F%2Fexample-app.com%2Fauth
                  &scope=openid

                 */
                .withStickySessionsEnabled(true)
                .withHealthCheckIntervalSeconds(30)
                /*
                Disabled self signup for admins to only be able to add new users to the user pool.
                The application will act as such an admin and create our users. For this to work
                we have to expand the IAM role for our ECS task and allow our application to
                perform all operations related to the identity provider
                 */
                .withTaskRolePolicyStatements(List.of(
                        PolicyStatement.Builder.create()
                                .sid("AllowCreatingUsers")
                                .effect(Effect.ALLOW)
                                .resources(
                                        List.of(String.format("arn:aws:cognito-idp:%s:%s:userpool/%s", region, accountId, cognitoOutputParameters.getUserPoolId()))
                                )
                                .actions(List.of(
                                        "cognito-idp:AdminCreateUser"
                                ))
                                .build())
                );

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

    static Map<String, String> environmentVariables(String springProfile, CognitoStack.CognitoOutputParameters cognitoOutputParameters) {
        Map<String, String> vars = new HashMap<>();
        vars.put("SPRING_PROFILES_ACTIVE", springProfile);
        vars.put("COGNITO_CLIENT_ID", cognitoOutputParameters.getUserPoolClientId());
        vars.put("COGNITO_CLIENT_SECRET", cognitoOutputParameters.getUserPoolClientSecret());
        vars.put("COGNITO_USER_POOL_ID", cognitoOutputParameters.getUserPoolId());
        vars.put("COGNITO_LOGOUT_URL", cognitoOutputParameters.getLogoutUrl());
        vars.put("COGNITO_PROVIDER_URL", cognitoOutputParameters.getProviderUrl());
        return vars;
    }

    static Environment makeEnv(String account, String region) {
        return Environment.builder()
                .account(account)
                .region(region)
                .build();
    }
}
