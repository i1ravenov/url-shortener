package com.example.repository;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public class UrlRepository {

    private final DynamoDbClient dynamoDb;
    private final String tableName;

    public UrlRepository(String tableName) {
        this.dynamoDb = DynamoDbClient.create();
        this.tableName = tableName;
    }

    public boolean save(String code, String originalUrl, Instant expiresAt) {

        Map<String, AttributeValue> item = Map.of(
                "code", AttributeValue.builder()
                        .s(code).build(),

                "originalUrl", AttributeValue.builder()
                        .s(originalUrl).build(),

                "createdAt", AttributeValue.builder()
                        .s(Instant.now().toString()).build(),

                "expiresAt", AttributeValue.builder()
                        .n(String.valueOf(expiresAt.getEpochSecond())).build()
        );

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .conditionExpression("attribute_not_exists(code)")
                .build();
        try {
            dynamoDb.putItem(request);
            return true;
        } catch (ConditionalCheckFailedException exception) {
            return false;
        }
    }

    public Optional<String> findOriginalUrl(String code) {

        GetItemResponse response = dynamoDb.getItem(
                GetItemRequest.builder()
                        .tableName(tableName)
                        .key(Map.of(
                                "code",
                                AttributeValue.builder().s(code).build()
                        ))
                        .build()
        );

        if (!response.hasItem()) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.item()
                .get("originalUrl").s());
    }
}
