package uk.gov.dvsa.motr.notifier.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.notifier.module.ConfigModule;
import uk.gov.dvsa.motr.notifier.module.InvocationContextModule;
import uk.gov.dvsa.motr.notifier.processing.unloader.NotifierReport;
import uk.gov.dvsa.motr.notifier.processing.unloader.QueueUnloader;

public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    public NotifierReport handle(Object request, Context context)  {

        logger.info("Request: {}, context: {}", request, context);
        Injector injector = Guice.createInjector(
                new InvocationContextModule(context),
                new ConfigModule()
        );

        return injector.getInstance(QueueUnloader.class).run(context);
    }
}
