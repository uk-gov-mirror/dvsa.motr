package uk.gov.dvsa.motr.test.integration.subscriptionloader;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.dvsa.motr.subscriptionloader.handler.EventHandler;
import uk.gov.dvsa.motr.subscriptionloader.handler.LoaderInvocationEvent;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.sqs.SqsHelper;

import java.util.List;
import java.util.UUID;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.dynamoDbClient;


public abstract class SubscriptionLoaderBase {

    private ObjectMapper jsonMapper = new ObjectMapper();

    private SqsHelper queueHelper;

    protected EventHandler eventHandler;

    protected DynamoDbFixture fixture;

    protected LoaderInvocationEvent buildRequest(String testTime) {

        return new LoaderInvocationEvent().setTime(testTime).setPurge(false);
    }

    protected Context buildContext() {
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

    public void setUp() {
        queueHelper = new SqsHelper();
        eventHandler = new EventHandler();
        fixture = new DynamoDbFixture(dynamoDbClient());
    }

    protected Subscription getQueuedSubscription() throws java.io.IOException {
        List<Message> messages = queueHelper.getMessagesFromQueue();
        Subscription queuedSubscription = jsonMapper.readValue(messages.get(0).getBody(), Subscription.class);
        queueHelper.deleteMessageFromQueue(messages.get(0));
        return queuedSubscription;
    }
}
