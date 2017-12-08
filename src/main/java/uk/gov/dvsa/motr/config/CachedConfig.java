package uk.gov.dvsa.motr.config;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory cache for configuration.
 * Useful for config values that require expensive post-processing like remote decryption with KMS.
 */
public class CachedConfig implements Config {

    private final Config wrappedConfig;

    private final Map<ConfigKey, String> cache = new HashMap<>();

    public CachedConfig(Config wrappedConfig) {

        this.wrappedConfig = wrappedConfig;
    }

    @Override
    public String getValue(ConfigKey key) {

        return cache.computeIfAbsent(key, wrappedConfig::getValue);
    }
}
