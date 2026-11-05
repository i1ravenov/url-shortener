package com.example.service;

import com.example.repository.UrlRepository;
import com.example.util.ShortCodeGenerator;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;

public class UrlService {

    private static final long EXPIRATION_TIME_IN_S = 900;
    private static final int CODE_LENGTH = 6;
    private static final int ATTEMPTS_TO_CODE = 5;
    private final UrlRepository repository;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }

    public String createShortUrl(String originalUrl) {

        validateUrl(originalUrl);

        for (int attempt = 0; attempt < ATTEMPTS_TO_CODE; attempt++) {
            String code = ShortCodeGenerator.generate(CODE_LENGTH);

            Instant expiresAt = Instant.now().plusSeconds(EXPIRATION_TIME_IN_S);
            if (repository.save(code, originalUrl, expiresAt)) {
                return code;
            }
        }

        throw new IllegalStateException(
                "Unable to generate unique short code"
        );
    }

    public Optional<String> getOriginalUrl(String code) {
        return repository.findOriginalUrl(code);
    }

    private void validateUrl(String url) {
        URI uri = URI.create(url);

        if (!"http".equalsIgnoreCase(uri.getScheme())
                && !"https".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException(
                    "Only HTTP and HTTPS URLs are allowed"
            );
        }

        if (uri.getHost() == null) {
            throw new IllegalArgumentException("Invalid URL");
        }
    }
}
