package uk.gov.dvsa.motr.test.integration.message;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionDbItem;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.subscriptionTableName;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.dynamoDbClient;

public class SubscriptionQueueMessageTest extends SubscriptionQueueMessageAbstractTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new TestEnvironmentVariables();

    private static final LocalDate MOCK_API_RANDOM_VEHICLE_DATE = LocalDate.of(2026, 3, 9);
    private static final LocalDate DATE_NOT_MATCHING_VEHICLE_MOCK = LocalDate.of(2015, 5, 15);
    private static final LocalDate MOCK_API_SPECIFIC_VEHICLE_DATE = LocalDate.of(2016, 11, 26);
    private static final LocalDate MOCK_DELETE_SUBSCRIPTION_DATE = LocalDate.of(2021, 11, 26);

    @Test
    public void whenAnEmailItemIsInTheDb_TheLoaderAddsToQueue_ThenTheNotifierSuccessfullyProcessesIt()
            throws Exception {

        subscriptionItem = new SubscriptionItem()
                .setMotDueDate(MOCK_API_RANDOM_VEHICLE_DATE)
                .setRandomMotTestNumber();

        saveAndProcessSubscriptionItem(subscriptionItem);
    }

    @Test
    public void whenAnSmsItemIsInTheDb_TheLoaderAddsToQueue_ThenTheNotifierSuccessfullyProcessesIt()
            throws Exception {

        subscriptionItem = new SubscriptionItem()
                .setMotDueDate(MOCK_API_RANDOM_VEHICLE_DATE)
                .setEmail("07000000000")
                .setContactType(SubscriptionQueueItem.ContactType.MOBILE)
                .setRandomMotTestNumber();

        saveAndProcessSubscriptionItem(subscriptionItem);
    }

    @Test
    public void whenProcessingASubscriptionWithAMismatchedMotDueDate_TheSubscriptionDateInTheDbIsUpdated() throws Exception {

        subscriptionItem = new SubscriptionItem();
        subscriptionItem
            .setMotDueDate(DATE_NOT_MATCHING_VEHICLE_MOCK)
            .setVrm("SUB-CHANGE")
            .setMotTestNumber("12345");

        SubscriptionDbItem changedSubscriptionDbItem = saveAndProcessSubscriptionItem(subscriptionItem);

        // Assert that the db subscription date now is equal to the mock api date.
        assertEquals(MOCK_API_SPECIFIC_VEHICLE_DATE, changedSubscriptionDbItem.getMotDueDate());
    }

    @Test
    public void whenProcessingASubscriptionWithAMismatchedMotTestNumber_TheSubscriptionMotTestNumberInTheDbIsUpdated() throws Exception {

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
    public void whenProcessingASubscriptionWithAMismatchedVrm_TheSubscriptionVrmInTheDbIsUpdated() throws Exception {

        subscriptionItem = new SubscriptionItem()
                .setMotDueDate(MOCK_API_SPECIFIC_VEHICLE_DATE)
                .setMotTestNumber("987654321012")
                .setVehicleType(VehicleType.MOT);

        SubscriptionDbItem changedSubscriptionDbItem = saveAndProcessSubscriptionItem(subscriptionItem);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :email")
                .withValueMap(
                        new ValueMap()
                                .withString(":vrm", changedSubscriptionDbItem.getVrm())
                                .withString(":email", changedSubscriptionDbItem.getEmail()
                        )
                );

        Item savedItem = new DynamoDB(dynamoDbClient()).getTable(subscriptionTableName()).query(spec).iterator().next();

        // Assert the new  db item has the same id subscriptionItem
        // (vrm update requires new record to be created, but want to keep original id).
        assertEquals(subscriptionItem.getId(), changedSubscriptionDbItem.getId());

        // Assert the db vrm now is equal to the mock api vrm.
        assertEquals("XXXYYY", changedSubscriptionDbItem.getVrm());
        assertEquals(VehicleType.MOT.name(), savedItem.getString("vehicle_type"));
        assertNotNull("created_at cannot be null when updating vrm", savedItem.getString("created_at"));
        assertNotNull("updated_at cannot be null when updating vrm", savedItem.getString("updated_at"));
        assertNotNull("contact_type cannot be null when updating vrm", savedItem.getString("contact_type"));
    }

    @Test
    public void whenProcessingASubscriptionWithDeletionRequired_TheSubscriptionInTheDbIsDeleted() throws Exception {

        subscriptionItem = new SubscriptionItem().setMotTestNumber("12345");

        // The actual MOT date will come from the mock ( = MOCK_API_SPECIFIC_VEHICLE_DATE).
        // (MOCK_DELETE_SUBSCRIPTION_DATE - one month) is used to trigger the loader and set the requestDate in ProcessSubscription.
        subscriptionItem.setMotDueDate(MOCK_DELETE_SUBSCRIPTION_DATE);

        SubscriptionDbItem changedSubscriptionDbItem = saveAndProcessSubscriptionItem(subscriptionItem);
        assertNull(changedSubscriptionDbItem);
    }

    @Test
    public void whenProcessingASubscriptionWithADvlaId_whichHasNowGotATestNumber_thenTheSubscriptionIsSuccessfullyProcessed()
            throws Exception {

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
            throws Exception {

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
            throws Exception {

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
}
