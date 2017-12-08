package uk.gov.dvsa.motr.subscriptionloader.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.inject.Guice;
import com.google.inject.Injector;

import uk.gov.dvsa.motr.subscriptionloader.module.ConfigModule;
import uk.gov.dvsa.motr.subscriptionloader.module.InvocationContextModule;
import uk.gov.dvsa.motr.subscriptionloader.module.LoaderModule;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.LoadReport;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.Loader;

public class EventHandler {

    public EventHandler() {
    }

    public LoadReport handle(LoaderInvocationEvent request, Context context) throws Exception {

        Injector injector = Guice.createInjector(
                new InvocationContextModule(context),
                new ConfigModule(),
                new LoaderModule(request.isPurge())
        );

        return injector.getInstance(Loader.class).run(request.getTimeAsDateTime().toLocalDate(), context);
    }
}
