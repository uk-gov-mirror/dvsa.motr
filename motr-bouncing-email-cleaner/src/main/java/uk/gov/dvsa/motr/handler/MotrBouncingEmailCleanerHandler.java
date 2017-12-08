package uk.gov.dvsa.motr.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.event.NotificationClientErrorEvent;
import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.module.ConfigModule;
import uk.gov.dvsa.motr.report.BouncingEmailCleanerReport;
import uk.gov.dvsa.motr.service.UnsubscribeBouncingEmailAddressService;
import uk.gov.service.notify.NotificationClientException;

import javax.ws.rs.ServerErrorException;

/**
 * Entry point of Lambda
 */
public class MotrBouncingEmailCleanerHandler {

    private static final Logger logger = LoggerFactory.getLogger(MotrBouncingEmailCleanerHandler.class);

    public BouncingEmailCleanerReport handleRequest(Object request, Context context) {

        logger.info("Request: {}, context: {}", request, context);

        Injector injector = Guice.createInjector(new ConfigModule());

        try {
            return injector.getInstance(UnsubscribeBouncingEmailAddressService.class).run();
        } catch (NotificationClientException e) {
            EventLogger.logErrorEvent(new NotificationClientErrorEvent(), e);
            throw new ServerErrorException(500);
        }
    }
}
