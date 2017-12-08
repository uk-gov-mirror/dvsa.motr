package uk.gov.dvsa.motr.web.config;

/**
 * Config based on Environment Variables
 */
public class EnvironmentVariableConfig implements Config {

    @Override
    public String getValue(ConfigKey key) {
        String value = System.getenv(key.getName());
        if (value == null) {
            throw new RuntimeException(
                    String.format("Config key: %s not specified!", key.getName()));
        }
        return value;
    }
}
