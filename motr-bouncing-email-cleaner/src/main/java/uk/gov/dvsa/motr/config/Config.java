package uk.gov.dvsa.motr.config;

/**
 * Generic interface for key-value configuration.
 */
public interface Config {

    /**
     * Returns config value if it exists.
     *
     * @param key strongly-typed config key
     * @return a configuration value
     */
    String getValue(ConfigKey key);
}
