package uk.gov.dvsa.motr.test.integration.message;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;

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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        // Invoke the notifiers handle event with correct date to read items from the queue.
        //NotifierReport report = eventHandler.handle(new SQSEvent(), buildContext());
        SubscriptionQueueItem subscriptionQueueItem = new SubscriptionQueueItem();

        ContactDetail contactDetail = new ContactDetail(subscriptionItem.getEmail(), subscriptionItem.getContactType());
        LocalDate date = LocalDate.parse(subscriptionItem.getMotDueDate().minusMonths(1).toString());

        subscriptionQueueItem.setMotTestNumber(subscriptionItem.getMotTestNumber());
        subscriptionQueueItem.setLoadedOnDate(date);
        subscriptionQueueItem.setContactDetail(contactDetail);
        subscriptionQueueItem.setDvlaId(subscriptionItem.getDvlaId());
        subscriptionQueueItem.setId(subscriptionItem.getId());
        subscriptionQueueItem.setVrm(subscriptionItem.getVrm());
        subscriptionQueueItem.setVehicleType(subscriptionItem.getVehicleType());
        subscriptionQueueItem.setMotDueDate(subscriptionItem.getMotDueDate());

        ObjectMapper jsonMapper = new ObjectMapper();
        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();

        message.setBody(jsonMapper.writeValueAsString(subscriptionQueueItem));
        List<SQSEvent.SQSMessage> messageList = Arrays.asList(message);

        SQSEvent event = new SQSEvent();
        event.setRecords(messageList);
        eventHandler.handle(event, buildContext());

        // @todo assert response code
        //assertEquals(1, report.getSuccessfullyProcessed());

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
}
