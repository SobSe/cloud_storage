package ru.sobse.cloud_storage.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenGeneratorImpl implements TokenGenerator{
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
