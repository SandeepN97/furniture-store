package com.example.furniturestore.service;

import com.launchdarkly.sdk.server.LDClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeatureFlagService {
    private final LDClient ldClient;

    public FeatureFlagService(@Value("${launchdarkly.sdk-key:}") String sdkKey) {
        this.ldClient = sdkKey == null || sdkKey.isEmpty() ? null : new LDClient(sdkKey);
    }

    public boolean isEnabled(String flagKey, boolean defaultValue) {
        if (ldClient == null) {
            return defaultValue;
        }
        return ldClient.boolVariation(flagKey, null, defaultValue);
    }
}
