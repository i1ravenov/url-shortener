package com.example.infra;

import software.amazon.awscdk.*;
import software.amazon.awscdk.aws_apigatewayv2_integrations.HttpLambdaIntegration;
import software.amazon.awscdk.services.apigatewayv2.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class UrlShortenerStack extends Stack {
    public UrlShortenerStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public UrlShortenerStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Table table = Table.Builder.create(this, "UrlMappings")
                .tableName("UrlMappings")
                .partitionKey(Attribute.builder()
                        .name("code")
                        .type(AttributeType.STRING)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .timeToLiveAttribute("expiresAt")
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        Function handler = Function.Builder.create(this, "UrlHandler")
                .runtime(new Runtime("java21"))
                .handler("com.example.handler.UrlHandler::handleRequest")
                .code(Code.fromAsset("../backend/target/url-shortener-backend-1.0-SNAPSHOT.jar"))
                .memorySize(512)
                .timeout(Duration.seconds(10))
                .environment(Map.of(
                        "TABLE_NAME", table.getTableName()
                ))
                .build();

        table.grantReadWriteData(handler);

        HttpLambdaIntegration integration =
                HttpLambdaIntegration.Builder.create(
                        "UrlIntegration",
                        handler
                ).build();

        HttpApi api = HttpApi.Builder.create(this, "UrlApi")
                .defaultIntegration(integration)
                .build();

        api.addRoutes(AddRoutesOptions.builder()
                .path("/urls")
                .methods(List.of(HttpMethod.POST))
                .integration(integration)
                .build());

        api.addRoutes(AddRoutesOptions.builder()
                .path("/{code}")
                .methods(List.of(HttpMethod.GET))
                .integration(integration)
                .build());

        CfnOutput.Builder.create(this, "ApiEndpoint")
                .value(api.getApiEndpoint())
                .description("URL Shortener API endpoint")
                .build();
    }
}
