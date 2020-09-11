package uk.gov.dvsa.motr.subscriptionloader.module;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import uk.gov.dvsa.motr.config.Config;
import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.Dispatcher;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.DefaultLoader;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.Loader;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.PurgingLoader;
import uk.gov.dvsa.motr.subscriptionloader.processing.producer.SubscriptionProducer;

import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.HGV_PSV_SUBSCRIPTION_LOADER;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.POST_PURGE_DELAY;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.QUEUE_URL;

import static java.lang.Integer.parseInt;

public class LoaderModule extends AbstractModule {

    private boolean isPurge = false;

    public LoaderModule(boolean isPurge) {
        this.isPurge = isPurge;
    }

    @Override
    protected void configure() {

    }

    @Provides
    public Loader provideLoader(Config config, AmazonSQSAsync client, SubscriptionProducer producer, Dispatcher dispatcher) {

        Loader loader = new DefaultLoader(producer, dispatcher, Boolean.parseBoolean(config.getValue(HGV_PSV_SUBSCRIPTION_LOADER)));

        String queueUrl = config.getValue(QUEUE_URL);
        int postPurgeDelayMs = parseInt(config.getValue(POST_PURGE_DELAY));
        int purgeInProgressDelayMs = 10_000;

        return isPurge ? new PurgingLoader(loader, client, queueUrl, postPurgeDelayMs, purgeInProgressDelayMs) : loader;
    }
}
