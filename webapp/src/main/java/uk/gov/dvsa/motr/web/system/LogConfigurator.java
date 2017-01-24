package uk.gov.dvsa.motr.web.system;

import com.amazonaws.services.lambda.runtime.Context;

import org.slf4j.MDC;

import uk.gov.dvsa.motr.web.config.Config;

import static org.apache.log4j.Level.toLevel;
import static org.apache.log4j.Logger.getRootLogger;

import static uk.gov.dvsa.motr.web.system.SystemVariable.LOG_LEVEL;

/**
 * Configures logging level according to the configuration.
 */
public class LogConfigurator {

    private static final String DEFAULT_LOG_LEVEL = "INFO";

    public static void configureLogging(Config config) {

        String logLevel = config.getValue(LOG_LEVEL).orElse(DEFAULT_LOG_LEVEL);
        getRootLogger().setLevel(toLevel(logLevel));
    }

    public static void setRequestContext(Context context) {
        MDC.put("lambdaFunctionName", context.getFunctionName());
    }
}
