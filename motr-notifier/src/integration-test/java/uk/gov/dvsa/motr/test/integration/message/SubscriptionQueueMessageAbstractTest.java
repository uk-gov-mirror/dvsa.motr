package uk.gov.dvsa.motr.test.integration.message;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.QueueAttributeName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;

import uk.gov.dvsa.motr.config.Config;
import uk.gov.dvsa.motr.config.EnvironmentVariableConfig;
import uk.gov.dvsa.motr.notifier.SystemVariable;
import uk.gov.dvsa.motr.notifier.component.subscription.persistence.DynamoDbSubscriptionRepository;
import uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionDbItem;
import uk.gov.dvsa.motr.notifier.handler.EventHandler;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionTable;
import uk.gov.dvsa.motr.test.integration.lambda.LoaderHelper;
import uk.gov.dvsa.motr.test.integration.lambda.LoaderInvocationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.region;
import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.subscriptionTableName;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.dynamoDbClient;

public abstract class SubscriptionQueueMessageAbstractTest {

    private static final long INITIAL_SQS_WARMUP_WAIT = 10;
    private static final long MAXIMUM_QUEUE_TRIES = 10;
    private static final long SECONDS_BETWEEN_QUEUE_RETRIES = 5;
    private static final long SECONDS_TO_WAIT_FOR_LAMBDA_EXECUTION = 10;

    private ObjectMapper jsonMapper = new ObjectMapper();
    private LoaderHelper loaderHelper;
    private EventHandler eventHandler;
    private DynamoDbFixture fixture;
    private DynamoDbSubscriptionRepository repo;

    protected SubscriptionItem subscriptionItem;

    @Before
    public void setUp() {

        repo = new DynamoDbSubscriptionRepository(subscriptionTableName(), region());
        loaderHelper = new LoaderHelper();
        eventHandler = new EventHandler();
        fixture = new DynamoDbFixture(dynamoDbClient());
    }

    @After
    public void cleanUp() {

        fixture.removeItem(subscriptionItem);
    }

    protected String buildLoaderRequest(String testTime) throws JsonProcessingException {

        LoaderInvocationEvent loaderInvocationEvent = new LoaderInvocationEvent(testTime);
        return jsonMapper.writeValueAsString(loaderInvocationEvent);
    }

    protected void waitForSqsProcessing() throws Exception {
        Thread.sleep(INITIAL_SQS_WARMUP_WAIT * 1000);

        int tries = 0;

        while (hasMessagesInQueue()) {
            if (tries >= MAXIMUM_QUEUE_TRIES) {
                System.out.println("Reached maximum attempts of waiting for the SQS to finish.");
                break;
            }

            System.out.println("Messages still to be processed in the queue. Waiting...");
            Thread.sleep(SECONDS_BETWEEN_QUEUE_RETRIES * 1000);

            ++tries;
        }
    }

    protected boolean hasMessagesInQueue() {
        Config config = new EnvironmentVariableConfig();

        AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();

        List<String> attributes = new ArrayList<>();
        attributes.add(QueueAttributeName.ApproximateNumberOfMessagesDelayed.toString());
        attributes.add(QueueAttributeName.ApproximateNumberOfMessagesNotVisible.toString());
        attributes.add(QueueAttributeName.ApproximateNumberOfMessages.toString());

        GetQueueAttributesResult getQueueAttributesResult = sqsClient
                .getQueueAttributes(config.getValue(SystemVariable.SUBSCRIPTIONS_QUEUE_URL), attributes);

        boolean hasDelayedMessages = !getQueueAttributesResult.getAttributes()
                .get(QueueAttributeName.ApproximateNumberOfMessagesDelayed.toString()).equals("0");

        boolean hasNotVisibleMessages = !getQueueAttributesResult.getAttributes()
                .get(QueueAttributeName.ApproximateNumberOfMessagesNotVisible.toString()).equals("0");

        boolean hasMessages = !getQueueAttributesResult.getAttributes()
                .get(QueueAttributeName.ApproximateNumberOfMessages.toString()).equals("0");

        System.out.println("Messages ready for processing: " + getQueueAttributesResult.getAttributes()
                .get(QueueAttributeName.ApproximateNumberOfMessages.toString()));
        System.out.println("Delayed messages: " + getQueueAttributesResult.getAttributes()
                .get(QueueAttributeName.ApproximateNumberOfMessagesDelayed.toString()));
        System.out.println("Not visible messages: " + getQueueAttributesResult.getAttributes()
                .get(QueueAttributeName.ApproximateNumberOfMessagesNotVisible.toString()));

        return hasDelayedMessages || hasNotVisibleMessages || hasMessages;
    }

    /**
     * Processes a subscriptionItem. Saves to db, processes, returns updated version (or null if was deleted).
     * @param subscriptionItem The subscription item to save.
     */
    protected SubscriptionDbItem saveAndProcessSubscriptionItem(SubscriptionItem subscriptionItem)
            throws Exception {

        // Save the subscription to db.
        fixture.table(new SubscriptionTable().item(subscriptionItem)).run();

        // Invoke the subscription loader with correct date to load items from db into queue.
        String testTime = subscriptionItem.getMotDueDate().minusMonths(1) + "T12:00:00Z";
        loaderHelper.invokeLoader(buildLoaderRequest(testTime));

        // Allow SQS to process the loaded items.
        waitForSqsProcessing();

        // Wait for DynamoDB to get it's act together.
        Thread.sleep(SECONDS_TO_WAIT_FOR_LAMBDA_EXECUTION * 1000);

        Optional<SubscriptionDbItem> subscriptionContainer = repo.findById(subscriptionItem.getId());
        if (!subscriptionContainer.isPresent()) {
            return null;
        }

        SubscriptionDbItem changedSubscriptionDbItem = subscriptionContainer.get();

        // Update vrm in subscriptionItem to match changeSubscriptionDbItem so cleanUp() will find it in the db.
        subscriptionItem.setVrm(changedSubscriptionDbItem.getVrm());
        return changedSubscriptionDbItem;
    }
}
