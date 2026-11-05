package com.example.model;

public class UrlMapping {
    private final String code;
    private final String originalUrl;
    private final String shortUrl;

    public UrlMapping(String code, String originalUrl, String shortUrl) {
        this.code = code;
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
    }

    public String getCode() {
        return code;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }
}
