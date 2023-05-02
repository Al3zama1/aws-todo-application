package com.abranlezama.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;

import static com.abranlezama.cdk.Validations.requireNonEmpty;

public class CertificateApp {
    public static void main(String[] args) {
        App app = new App();

        String environmentName = (String) app.getNode().tryGetContext("environmentName");
        requireNonEmpty(environmentName, "context variable 'environmentName' must not be null");

        String applicationName = (String) app.getNode().tryGetContext("applicationName");
        requireNonEmpty(applicationName, "context variable 'applicationName' must not be null");

        String accountId = (String) app.getNode().tryGetContext("accountId");
        requireNonEmpty(accountId, "context variable 'accountId' must not be null");

        String region = (String) app.getNode().tryGetContext("region");
        requireNonEmpty(region, "context variable 'region' must not be null");

        String hostedZoneDomain = (String) app
                .getNode()
                .tryGetContext("hostedZoneDomain");
        requireNonEmpty(hostedZoneDomain, "context variable 'hostedDomainZone' must not be null");

        String applicationDomain = (String) app
                .getNode()
                .tryGetContext("applicationDomain");
        requireNonEmpty(applicationDomain, "context variable 'applicationDomain' must not be null");

        Environment awsEnvironment = makeEnv(accountId, region);

        ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
                applicationName,
                environmentName
        );

        new CertificateStack(
                app,
                "certificate",
                awsEnvironment,
                applicationEnvironment,
                applicationDomain,
                hostedZoneDomain
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
