package com.example.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.example.model.CreateUrlRequest;
import com.example.model.UrlMapping;
import com.example.repository.UrlRepository;
import com.example.service.UrlService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;

public class UrlHandler {

    private static final ObjectMapper MAPPER =
            new ObjectMapper();

    private static final UrlRepository REPOSITORY =
            new UrlRepository(
                    System.getenv("TABLE_NAME")
            );

    private static final UrlService SERVICE =
            new UrlService(REPOSITORY);

    public APIGatewayV2HTTPResponse handleRequest(
            APIGatewayV2HTTPEvent event,
            Context context) throws JsonProcessingException {

        String method = event.getRequestContext()
                .getHttp().getMethod();

        String path = event.getRawPath();

        if ("POST".equals(method) && "/urls".equals(path)) {
            return createUrl(event);
        }

        if ("GET".equals(method)) {
            String code = path.substring(1);
            return redirect(code);
        }

        return response(404, "Not found");
    }

    private APIGatewayV2HTTPResponse response(int status, String notFound) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(status)
                .withBody(notFound)
                .build();
    }

    private APIGatewayV2HTTPResponse redirect(String code) {
        Optional<String> originalUrl = SERVICE.getOriginalUrl(code);
        if (originalUrl.isEmpty()) {
            return response(404, "Not found");
        }
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(302)
                .withHeaders(Map.of("Location", originalUrl.get()))
                .build();
    }

    private APIGatewayV2HTTPResponse createUrl(APIGatewayV2HTTPEvent event) throws JsonProcessingException {
        CreateUrlRequest urlRequest = MAPPER.readValue(event.getBody(), CreateUrlRequest.class);

        String code = SERVICE.createShortUrl(urlRequest.getUrl());
        String body = MAPPER.writeValueAsString(new UrlMapping(code, urlRequest.getUrl(), System.getenv("BASE_API_URL") + "/" + code));
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(201)
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody(body)
                .build();
    }
}
