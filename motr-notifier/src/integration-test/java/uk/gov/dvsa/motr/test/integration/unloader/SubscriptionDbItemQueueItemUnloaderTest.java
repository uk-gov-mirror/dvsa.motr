package uk.gov.dvsa.motr.test.integration.unloader;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.notifier.component.subscription.persistence.DynamoDbSubscriptionRepository;
import uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionDbItem;
import uk.gov.dvsa.motr.notifier.handler.EventHandler;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.unloader.NotifierReport;
import uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionTable;
import uk.gov.dvsa.motr.test.integration.lambda.LoaderHelper;
import uk.gov.dvsa.motr.test.integration.lambda.LoaderInvocationEvent;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.region;
import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.subscriptionTableName;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.dynamoDbClient;

public class SubscriptionDbItemQueueItemUnloaderTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new TestEnvironmentVariables();

    private static final LocalDate MOCK_API_RANDOM_VEHICLE_DATE = LocalDate.of(2026, 3, 9);
    private static final LocalDate DATE_NOT_MATCHING_VEHICLE_MOCK = LocalDate.of(2015, 5, 15);
    private static final LocalDate MOCK_API_SPECIFIC_VEHICLE_DATE = LocalDate.of(2016, 11, 26);
    private static final LocalDate MOCK_DELETE_SUBSCRIPTION_DATE = LocalDate.of(2021, 11, 26);

    private ObjectMapper jsonMapper = new ObjectMapper();
    private LoaderHelper loaderHelper;
    private EventHandler eventHandler;
    private SubscriptionItem subscriptionItem;
    private DynamoDbFixture fixture;
    private DynamoDbSubscriptionRepository repo;

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

    @Test
    public void whenAnEmailItemIsInTheDb_TheLoaderAddsToQueue_ThenTheNotifierSuccessfullyProcessesIt()
            throws IOException, InterruptedException, ExecutionException, NotificationClientException {

        subscriptionItem = new SubscriptionItem()
                .setMotDueDate(MOCK_API_RANDOM_VEHICLE_DATE)
                .setRandomMotTestNumber();

        saveAndProcessSubscriptionItem(subscriptionItem);
    }

    @Test
    public void whenAnSmsItemIsInTheDb_TheLoaderAddsToQueue_ThenTheNotifierSuccessfullyProcessesIt()
            throws IOException, InterruptedException, ExecutionException, NotificationClientException {

        subscriptionItem = new SubscriptionItem()
                .setMotDueDate(MOCK_API_RANDOM_VEHICLE_DATE)
                .setEmail("07000000000")
                .setContactType(SubscriptionQueueItem.ContactType.MOBILE)
                .setRandomMotTestNumber();

        saveAndProcessSubscriptionItem(subscriptionItem);
    }

    @Test
    public void whenProcessingASubscriptionWithAMismatchedMotDueDate_TheSubscriptionDateInTheDbIsUpdated() throws IOException,
            InterruptedException, ExecutionException, NotificationClientException {

        subscriptionItem = new SubscriptionItem();
        subscriptionItem
            .setMotDueDate(DATE_NOT_MATCHING_VEHICLE_MOCK)
            .setVrm("WDD2040022A65")
            .setMotTestNumber("12345");

        SubscriptionDbItem changedSubscriptionDbItem = saveAndProcessSubscriptionItem(subscriptionItem);

        // Assert that the db subscription date now is equal to the mock api date.
        assertEquals(MOCK_API_SPECIFIC_VEHICLE_DATE, changedSubscriptionDbItem.getMotDueDate());
    }

    @Test
    public void whenProcessingASubscriptionWithAMismatchedMotTestNumber_TheSubscriptionMotTestNumberInTheDbIsUpdated() throws IOException,
            InterruptedException, ExecutionException, NotificationClientException {

        subscriptionItem = new SubscriptionItem();
        subscriptionItem
            .setMotDueDate(MOCK_API_SPECIFIC_VEHICLE_DATE)
            .setVrm("XXXYYY")
            .setMotTestNumber("987654321012");

        SubscriptionDbItem changedSubscriptionDbItem = saveAndProcessSubscriptionItem(subscriptionItem);

        // Assert that the db motTestNumber now is equal to the mock api motTestNumber.
        assertEquals("2321321", changedSubscriptionDbItem.getMotTestNumber());
    }

    @Test
    public void whenProcessingASubscriptionWithAMismatchedVrm_TheSubscriptionVrmInTheDbIsUpdated() throws IOException,
            InterruptedException, ExecutionException, NotificationClientException {

        subscriptionItem = new SubscriptionItem()
                .setMotDueDate(MOCK_API_SPECIFIC_VEHICLE_DATE)
                .setMotTestNumber("987654321012");

        SubscriptionDbItem changedSubscriptionDbItem = saveAndProcessSubscriptionItem(subscriptionItem);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :email")
                .withValueMap(new ValueMap().withString(":vrm", changedSubscriptionDbItem.getVrm()).withString(":email",
                changedSubscriptionDbItem.getEmail()));

        Item savedItem = new DynamoDB(dynamoDbClient()).getTable(subscriptionTableName()).query(spec).iterator().next();

        // Assert the new  db item has the same id subscriptionItem
        // (vrm update requires new record to be created, but want to keep original id).
        assertEquals(subscriptionItem.getId(), changedSubscriptionDbItem.getId());

        // Assert the db vrm now is equal to the mock api vrm.
        assertEquals("XXXYYY", changedSubscriptionDbItem.getVrm());
        assertNotNull("created_at cannot be null when updating vrm", savedItem.getString("created_at"));
        assertNotNull("updated_at cannot be null when updating vrm", savedItem.getString("updated_at"));
        assertNotNull("contact_type cannot be null when updating vrm", savedItem.getString("contact_type"));
    }

    @Test
    public void whenProcessingASubscriptionWithDeletionRequired_TheSubscriptionInTheDbIsDeleted() throws IOException,
            InterruptedException, ExecutionException, NotificationClientException {

        subscriptionItem = new SubscriptionItem().setMotTestNumber("12345");

        // The actual MOT date will come from the mock ( = MOCK_API_SPECIFIC_VEHICLE_DATE).
        // (MOCK_DELETE_SUBSCRIPTION_DATE - one month) is used to trigger the loader and set the requestDate in ProcessSubscription.
        subscriptionItem.setMotDueDate(MOCK_DELETE_SUBSCRIPTION_DATE);

        SubscriptionDbItem changedSubscriptionDbItem = saveAndProcessSubscriptionItem(subscriptionItem);
        assertNull(changedSubscriptionDbItem);
    }

    @Test
    public void whenProcessingASubscriptionWithADvlaId_whichHasNowGotATestNumber_thenTheSubscriptionIsSuccessfullyProcessed()
            throws IOException, InterruptedException, ExecutionException, NotificationClientException {

        subscriptionItem = new SubscriptionItem()
                .setMotDueDate(MOCK_API_SPECIFIC_VEHICLE_DATE)
                .setVrm("WDD2040022A65")
                .setDvlaId("12345");

        SubscriptionDbItem changedSubscriptionDbItem = saveAndProcessSubscriptionItem(subscriptionItem);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :email")
                .withValueMap(new ValueMap().withString(":vrm", changedSubscriptionDbItem.getVrm()).withString(":email",
                changedSubscriptionDbItem.getEmail()));

        Item savedItem = new DynamoDB(dynamoDbClient()).getTable(subscriptionTableName()).query(spec).iterator().next();

        // Assert the new  db item has the same id subscriptionItem
        // (vrm update requires new record to be created, but want to keep original id).
        assertEquals(subscriptionItem.getId(), changedSubscriptionDbItem.getId());

        // Assert the db vrm now is equal to the mock api vrm.
        assertEquals("WDD2040022A65", changedSubscriptionDbItem.getVrm());
        assertNotNull("mot_test_number cannot be null when trade api returns an motTestNumber", savedItem.getString("mot_test_number"));
        assertNull("dvla_id is not null even though there is a motTestNumber", savedItem.getString("dvla_id"));
    }

    @Test
    public void whenProcessingASubscriptionWithADvlaId_whichHasUndergoneACherishedTransfer_thenTheSubscriptionIsSuccessfullyProcessed()
            throws IOException, InterruptedException, ExecutionException, NotificationClientException {

        subscriptionItem = new SubscriptionItem()
                .setMotDueDate(MOCK_API_SPECIFIC_VEHICLE_DATE)
                .setDvlaId("12345");

        SubscriptionDbItem changedSubscriptionDbItem = saveAndProcessSubscriptionItem(subscriptionItem);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :email")
                .withValueMap(new ValueMap().withString(":vrm", changedSubscriptionDbItem.getVrm()).withString(":email",
                changedSubscriptionDbItem.getEmail()));

        Item savedItem = new DynamoDB(dynamoDbClient()).getTable(subscriptionTableName()).query(spec).iterator().next();

        // Assert the new  db item has the same id subscriptionItem
        // (vrm update requires new record to be created, but want to keep original id).
        assertEquals(subscriptionItem.getId(), changedSubscriptionDbItem.getId());

        // Assert the db vrm now is equal to the mock api vrm.
        assertEquals("WDD2040022A65", changedSubscriptionDbItem.getVrm());
        assertNotNull("mot_test_number cannot be null when trade api returns an motTestNumber", savedItem.getString("mot_test_number"));
        assertNull("dvla_id is not null even though there is a motTestNumber", savedItem.getString("dvla_id"));
    }

    @Test
    public void whenProcessingASubscriptionWithADvlaId_whichHasGotANewExpiryDate_thenTheSubscriptionIsSuccessfullyProcessed()
            throws IOException, InterruptedException, ExecutionException, NotificationClientException {

        subscriptionItem = new SubscriptionItem()
                .setMotDueDate(LocalDate.of(1991, 3, 9))
                .setVrm("SUP4R")
                .setDvlaId("412321");

        SubscriptionDbItem changedSubscriptionDbItem = saveAndProcessSubscriptionItem(subscriptionItem);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :email")
                .withValueMap(new ValueMap().withString(":vrm", changedSubscriptionDbItem.getVrm()).withString(":email",
                changedSubscriptionDbItem.getEmail()));

        Item savedItem = new DynamoDB(dynamoDbClient()).getTable(subscriptionTableName()).query(spec).iterator().next();

        // Assert the new  db item has the same id subscriptionItem
        // (vrm update requires new record to be created, but want to keep original id).
        assertEquals(subscriptionItem.getId(), changedSubscriptionDbItem.getId());

        // Assert the db vrm now is equal to the mock api vrm.
        assertEquals("mot due date is not updated", "2007-11-26", savedItem.getString("mot_due_date"));
        assertEquals("vrm is changed when it wasn't meant too", "SUP4R", changedSubscriptionDbItem.getVrm());
        assertNull("mot_test_number is not null, when it is meant to be", savedItem.getString("mot_test_number"));
        assertNotNull("dvla_id is null even though it is meant to be kept", savedItem.getString("dvla_id"));
    }

    private String buildLoaderRequest(String testTime) throws JsonProcessingException {

        LoaderInvocationEvent loaderInvocationEvent = new LoaderInvocationEvent(testTime);
        return jsonMapper.writeValueAsString(loaderInvocationEvent);
    }

    /**
     * Processes a subscriptionItem. Saves to db, processes, returns updated version (or null if was deleted).
     * @param subscriptionItem The subscription item to save.
     */
    private SubscriptionDbItem saveAndProcessSubscriptionItem(SubscriptionItem subscriptionItem)
            throws IOException, InterruptedException, ExecutionException, NotificationClientException {

        // Save the subscription to db.
        fixture.table(new SubscriptionTable().item(subscriptionItem)).run();

        // Invoke the subscription loader with correct date to load items from db into queue.
        String testTime = subscriptionItem.getMotDueDate().minusDays(30) + "T12:00:00Z";
        loaderHelper.invokeLoader(buildLoaderRequest(testTime));

        // Invoke the notifiers handle event with correct date to read items from the queue.
        NotifierReport report = eventHandler.handle(new Object(), buildContext());
        assertEquals(1, report.getSuccessfullyProcessed());

        Optional<SubscriptionDbItem> subscriptionContainer = repo.findById(subscriptionItem.getId());
        if (!subscriptionContainer.isPresent()) {
            return null;
        }

        SubscriptionDbItem changedSubscriptionDbItem = subscriptionContainer.get();

        // Update vrm in subscriptionItem to match changeSubscriptionDbItem so cleanUp() will find it in the db.
        subscriptionItem.setVrm(changedSubscriptionDbItem.getVrm());
        return changedSubscriptionDbItem;
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
}
