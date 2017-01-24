package uk.gov.dvsa.motr.web.config;


import java.util.Optional;

/**
 * Generic interface for key-value configuration.
 */
public interface Config {

    /**
     * Returns config value if it exists.
     *
     * @param key strongly-typed config key
     * @return a configuration value or Optional.none().
     */
    Optional<String> getValue(ConfigKey key);
}
