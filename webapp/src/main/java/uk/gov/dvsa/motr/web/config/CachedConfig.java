package uk.gov.dvsa.motr.web.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory cache for configuration.
 * Useful for config values that require expensive post-processing like remote decryption with KMS.
 */
public class CachedConfig implements Config {

    private final Config wrappedConfig;

    private final Map<ConfigKey, Optional<String>> cache = new HashMap<>();

    public CachedConfig(Config wrappedConfig) {
        this.wrappedConfig = wrappedConfig;
    }

    @Override
    public Optional<String> getValue(ConfigKey key) {
        return cache.computeIfAbsent(key, wrappedConfig::getValue);
    }
}
