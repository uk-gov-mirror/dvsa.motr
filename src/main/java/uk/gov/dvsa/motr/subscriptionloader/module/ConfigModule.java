package uk.gov.dvsa.motr.subscriptionloader.module;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.amazonaws.services.sqs.buffered.QueueBufferConfig;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import org.apache.log4j.Logger;

import uk.gov.dvsa.motr.config.CachedConfig;
import uk.gov.dvsa.motr.config.Config;
import uk.gov.dvsa.motr.config.EnvironmentVariableConfig;
import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.Dispatcher;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.DefaultLoader;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.Loader;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.PurgingLoader;
import uk.gov.dvsa.motr.subscriptionloader.processing.producer.DynamoDbProducer;
import uk.gov.dvsa.motr.subscriptionloader.processing.producer.SubscriptionProducer;

import static org.apache.log4j.Level.toLevel;

import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.INFLIGHT_BATCHES;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.LOG_LEVEL;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.QUEUE_URL;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.REGION;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.TABLE_NAME;


public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {

        Config config = new CachedConfig(new EnvironmentVariableConfig());
        bind(Config.class).toInstance(config);
        Logger.getRootLogger().setLevel(toLevel(config.getValue(LOG_LEVEL)));
    }


    @Provides
    public Dispatcher provideDispatcher(Config config, AmazonSQSAsync sqsClient, Context context) {

        int inFlightBatches = new Integer(config.getValue(INFLIGHT_BATCHES));
        String url = config.getValue(QUEUE_URL);

        QueueBufferConfig queueBuffer = new QueueBufferConfig().withMaxInflightOutboundBatches(inFlightBatches);
        AmazonSQSBufferedAsyncClient bufferedClient = new AmazonSQSBufferedAsyncClient(sqsClient, queueBuffer);

        return new Dispatcher(bufferedClient, url, context.getAwsRequestId());
    }

    @Provides
    public AmazonSQSAsync provideSqsClient(Config config) {

        String region = config.getValue(REGION);
        return AmazonSQSAsyncClientBuilder.standard().withRegion(region).build();
    }

    @Provides
    public SubscriptionProducer provideProducer(Config config) {

        String table = config.getValue(TABLE_NAME);
        String region = config.getValue(REGION);
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();
        return new DynamoDbProducer(client, table);
    }

    @Provides
    public Loader provideLoader(Config config, AmazonSQSAsync client, SubscriptionProducer producer, Dispatcher dispatcher) {

        return new PurgingLoader(new DefaultLoader(producer, dispatcher), client, config.getValue(QUEUE_URL));
    }
}
