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

import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.POST_PURGE_DELAY;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.QUEUE_URL;

public class LoaderModule extends AbstractModule {

    @Override
    protected void configure() {

    }

    @Provides
    public Loader provideLoader(Config config, AmazonSQSAsync client, SubscriptionProducer producer, Dispatcher dispatcher) {

        return new PurgingLoader(new DefaultLoader(producer, dispatcher), client, config.getValue(QUEUE_URL), Integer.parseInt(config
                .getValue(POST_PURGE_DELAY)), 10_000);
    }
}
