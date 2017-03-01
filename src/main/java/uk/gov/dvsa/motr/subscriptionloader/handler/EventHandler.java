package uk.gov.dvsa.motr.subscriptionloader.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.subscriptionloader.module.ConfigModule;
import uk.gov.dvsa.motr.subscriptionloader.module.InvocationContextModule;
import uk.gov.dvsa.motr.subscriptionloader.module.LoaderModule;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.LoadReport;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.Loader;

public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    private Module loaderModule = new LoaderModule();

    public EventHandler() {

    }

    public EventHandler(Module loaderModule) {

        this.loaderModule = loaderModule;
    }

    public LoadReport handle(AwsCloudwatchEvent request, Context context) throws Exception {

        logger.info("Request: {}, context: {}", request, context);
        Injector injector = Guice.createInjector(
                new InvocationContextModule(context),
                new ConfigModule(),
                loaderModule
        );

        return injector.getInstance(Loader.class).run(request.getTimeAsDateTime().toLocalDate(), context);
    }
}
