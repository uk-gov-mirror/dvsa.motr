package uk.gov.dvsa.motr.notifier.handler;

import com.amazonaws.services.lambda.runtime.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    public void handle(Object request, Context context) {

        logger.info("Request: {}, context: {}", request, context);
    }
}
