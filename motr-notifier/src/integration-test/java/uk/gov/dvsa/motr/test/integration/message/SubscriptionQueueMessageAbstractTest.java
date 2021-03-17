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
import uk.gov.dvsa.motr.notifier.processing.model.ContactDetail;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionTable;
import uk.gov.dvsa.motr.test.integration.lambda.LoaderHelper;
import uk.gov.dvsa.motr.test.integration.lambda.LoaderInvocationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.region;
import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.subscriptionTableName;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.dynamoDbClient;

public abstract class SubscriptionQueueMessageAbstractTest {

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
        int tries = 0;

        while (hasMessagesInQueue()) {
            if (tries > 5) {
                System.out.println("Reach maximum attempts of waiting for the SQS to finish.");
                // Maybe throw an exception here to prevent the rest of the tests from processing.
                // Something is wrong.
                break;
            }

            System.out.println("Messages still in queue. Waiting 5 seconds...");
            Thread.sleep(5000);

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

        System.out.println("Delayed messages: " + getQueueAttributesResult.getAttributes()
                .get(QueueAttributeName.ApproximateNumberOfMessagesDelayed.toString()));
        System.out.println("Not visible messages: " + getQueueAttributesResult.getAttributes()
                .get(QueueAttributeName.ApproximateNumberOfMessagesNotVisible.toString()));
        System.out.println("Total messages: " + getQueueAttributesResult.getAttributes()
                .get(QueueAttributeName.ApproximateNumberOfMessages.toString()));

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

        waitForSqsProcessing();

        Optional<SubscriptionDbItem> subscriptionContainer = repo.findById(subscriptionItem.getId());
        if (!subscriptionContainer.isPresent()) {
            return null;
        }

        SubscriptionDbItem changedSubscriptionDbItem = subscriptionContainer.get();

        // Update vrm in subscriptionItem to match changeSubscriptionDbItem so cleanUp() will find it in the db.
        subscriptionItem.setVrm(changedSubscriptionDbItem.getVrm());
        return changedSubscriptionDbItem;
    }

    // Confirms that the data provided by the test is saved into a DB item successfully
    protected void verifySavedSubscriptionItem(SubscriptionItem subscriptionItem, SubscriptionDbItem subscriptionDbItem) {
        assertEquals(subscriptionItem.getId(), subscriptionDbItem.getId());
        assertEquals(subscriptionItem.getVrm(), subscriptionDbItem.getVrm());
        assertEquals(subscriptionItem.getEmail(), subscriptionDbItem.getEmail());
        assertEquals(subscriptionItem.getMotDueDate(), subscriptionDbItem.getMotDueDate());
        assertEquals(subscriptionItem.getVehicleType(), subscriptionDbItem.getVehicleType());
    }
}
