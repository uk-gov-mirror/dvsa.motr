package uk.gov.dvsa.motr.web.config;

import java.util.Optional;

/**
 * Config based on Environment Variables
 */
public class EnvironmentVariableConfig implements Config {

    @Override
    public Optional<String> getValue(ConfigKey key) {
        return Optional.ofNullable(System.getenv(key.getName()));
    }
}
