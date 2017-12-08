package uk.gov.dvsa.motr.eventlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Event log is responsible for recording and formatting strictly defined set of log events.
 * Each log event will have a set of parameters associated with it so metrics and alarms can base off it.
 */
public class EventLogger {

    private static final Logger logger = LoggerFactory.getLogger("EventLogger");

    public static void logEvent(Event event) {

        pushContext(event);
        logger.info(event.getCode());
        popContext(event);
    }

    public static void logErrorEvent(Event event, Exception exception) {

        pushContext(event);
        logger.error(event.getCode(), exception);
        popContext(event);
    }

    public static void logErrorEvent(Event event) {

        logErrorEvent(event, null);
    }

    private static void pushContext(Event event) {

        event.toMap().forEach((key, val) -> MDC.put(asCustomDatumKey(key), val));
    }

    private static void popContext(Event event) {

        event.toMap().keySet().forEach(key -> MDC.remove(asCustomDatumKey(key)));
    }

    private static String asCustomDatumKey(String key) {

        return "x-" + key;
    }
}
