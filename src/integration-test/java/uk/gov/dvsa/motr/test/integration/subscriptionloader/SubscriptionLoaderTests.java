package uk.gov.dvsa.motr.test.integration.subscriptionloader;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.config.Config;
import uk.gov.dvsa.motr.subscriptionloader.handler.AwsCloudwatchEvent;
import uk.gov.dvsa.motr.subscriptionloader.handler.EventHandler;
import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.Dispatcher;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.DefaultLoader;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.LoadReport;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.Loader;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.PurgingLoader;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;
import uk.gov.dvsa.motr.subscriptionloader.processing.producer.SubscriptionProducer;
import uk.gov.dvsa.motr.test.environment.variables.TestEnvironmentVariables;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionTable;
import uk.gov.dvsa.motr.test.integration.sqs.SqsHelper;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.POST_PURGE_DELAY;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.QUEUE_URL;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.dynamoDbClient;


public class SubscriptionLoaderTests {

    @Rule
    public final EnvironmentVariables environmentVariables = new TestEnvironmentVariables();

    private ObjectMapper jsonMapper = new ObjectMapper();
    private SqsHelper queueHelper;
    private EventHandler eventHandler;
    private SubscriptionItem subscriptionItem;

    @Before
    public void setUp() {

        queueHelper = new SqsHelper();
        eventHandler = new EventHandler(new IntegrationTestModule());
        subscriptionItem = new SubscriptionItem();
        DynamoDbFixture fixture = new DynamoDbFixture(dynamoDbClient());
        fixture.table(new SubscriptionTable().item(subscriptionItem)).run();
    }

    @Test
    public void runLoaderForOneMonthReminderThenEnsureItemsAddedToQueue() throws Exception {

        String testTime = subscriptionItem.getMotDueDate().minusMonths(1) + "T12:00:00Z";
        LoadReport loadReport = eventHandler.handle(buildRequest(testTime), buildContext());

        List<Message> messages = queueHelper.getMessagesFromQueue();

        Subscription subscription = jsonMapper.readValue(messages.get(0).getBody(), Subscription.class);
        queueHelper.deleteMessageFromQueue(messages.get(0));

        assertEquals(subscriptionItem.getVrm(), subscription.getVrm());
        assertEquals(subscriptionItem.getEmail(), subscription.getEmail());
        assertEquals(1, loadReport.getSubmittedForProcessing());
        assertEquals(1, loadReport.getProcessed());
    }

    @Test
    public void runLoaderForTwoWeeksReminderThenEnsureItemsAddedToQueue() throws Exception {

        String testTime = subscriptionItem.getMotDueDate().minusDays(14) + "T12:00:00Z";
        LoadReport loadReport = eventHandler.handle(buildRequest(testTime), buildContext());

        List<Message> messages = queueHelper.getMessagesFromQueue();

        Subscription subscription = jsonMapper.readValue(messages.get(0).getBody(), Subscription.class);
        queueHelper.deleteMessageFromQueue(messages.get(0));

        assertEquals(subscriptionItem.getVrm(), subscription.getVrm());
        assertEquals(subscriptionItem.getEmail(), subscription.getEmail());
        assertEquals(1, loadReport.getSubmittedForProcessing());
        assertEquals(1, loadReport.getProcessed());
    }

    private AwsCloudwatchEvent buildRequest(String testTime) {
        return new AwsCloudwatchEvent().setTime(testTime);
    }

    private Context buildContext() {
        return new Context() {
            @Override
            public String getAwsRequestId() {
                return UUID.randomUUID().toString();
            }

            @Override
            public String getLogGroupName() {
                return null;
            }

            @Override
            public String getLogStreamName() {
                return null;
            }

            @Override
            public String getFunctionName() {
                return null;
            }

            @Override
            public String getFunctionVersion() {
                return null;
            }

            @Override
            public String getInvokedFunctionArn() {
                return null;
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {

                return 400000;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 0;
            }

            @Override
            public LambdaLogger getLogger() {
                return null;
            }
        };
    }

    private class IntegrationTestModule extends AbstractModule {

        @Override
        protected void configure() {

        }

        @Provides
        public Loader provideLoader(Config config, AmazonSQSAsync client, SubscriptionProducer producer, Dispatcher dispatcher) {

            return new DefaultLoader(producer, dispatcher);
        }
    }

}
