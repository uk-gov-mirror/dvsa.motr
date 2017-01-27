package uk.gov.dvsa.motr.web.logging;

import uk.gov.dvsa.motr.web.config.Config;

import static org.apache.log4j.Level.toLevel;
import static org.apache.log4j.Logger.getRootLogger;

import static uk.gov.dvsa.motr.web.system.SystemVariable.LOG_LEVEL;

/**
 * Configures logging level according to the configuration.
 */
public class LogConfigurator {

    public static void configureLogging(Config config) {

        String logLevel = config.getValue(LOG_LEVEL);
        getRootLogger().setLevel(toLevel(logLevel));
    }
}
