package uk.gov.dvsa.motr.web.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.CancelledSubscriptionItem;
import uk.gov.dvsa.motr.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.web.component.subscription.model.CancelledSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.ContactDetail;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.cancelledSubscriptionTableName;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.client;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.region;

public class DynamoDbCancelledSubscriptionRepositoryTest {

    private static final String REASON_FOR_CANCELLATION_USER_CANCELLED = "User cancelled";

    CancelledSubscriptionRepository repository;
    DynamoDbFixture fixture;

    @Before
    public void setUp() {

        repository = new DynamoDbCancelledSubscriptionRepository(cancelledSubscriptionTableName(), region());
        fixture = new DynamoDbFixture(client());
    }

    @Test
    public void saveCancelledSubscriptionForMotVehicle_CorrectlySavesNonModelAttributesToDb() {

        CancelledSubscriptionItem cancelledSubscriptionItemForMotVehicle = new CancelledSubscriptionItem();

        MotIdentification motIdentification = new MotIdentification(cancelledSubscriptionItemForMotVehicle.getMotTestNumber(), null);

        CancelledSubscription cancelledSubscriptionForMotVehicle = new CancelledSubscription();
        cancelledSubscriptionForMotVehicle
                .setUnsubscribeId(cancelledSubscriptionItemForMotVehicle.getUnsubscribeId())
                .setContactDetail(new ContactDetail(cancelledSubscriptionItemForMotVehicle.getEmail(), Subscription.ContactType.EMAIL))
                .setVrm(cancelledSubscriptionItemForMotVehicle.getVrm())
                .setMotIdentification(motIdentification)
                .setReasonForCancellation(REASON_FOR_CANCELLATION_USER_CANCELLED);

        repository.save(cancelledSubscriptionForMotVehicle);

        String cancelledSubscriptionEmail = cancelledSubscriptionForMotVehicle.getContactDetail().getValue();
        ValueMap specValueMap = new ValueMap()
                .withString(":id", cancelledSubscriptionForMotVehicle.getUnsubscribeId())
                .withString(":email", cancelledSubscriptionEmail);
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("id = :id AND email = :email")
                .withValueMap(specValueMap);

        Item savedItem = new DynamoDB(client()).getTable(cancelledSubscriptionTableName()).query(spec).iterator().next();

        assertNotNull("cancelled_at cannot be null when saving db", savedItem.getString("cancelled_at"));

        assertEquals("Returned item does not match", cancelledSubscriptionEmail, savedItem.getString("email"));
    }

    @Test
    public void saveCancelledSubscriptionForDvlaVehicle_CorrectlySavesNonModelAttributesToDb() {

        CancelledSubscriptionItem cancelledSubscriptionItemForDvlaVehicle = new CancelledSubscriptionItem();
        cancelledSubscriptionItemForDvlaVehicle.setMotTestNumber(null);

        MotIdentification motIdentification = new MotIdentification(null, cancelledSubscriptionItemForDvlaVehicle.getDvlaId());

        CancelledSubscription cancelledSubscriptionForDvlaVehicle = new CancelledSubscription();
        cancelledSubscriptionForDvlaVehicle
                .setUnsubscribeId(cancelledSubscriptionItemForDvlaVehicle.getUnsubscribeId())
                .setContactDetail(new ContactDetail(cancelledSubscriptionItemForDvlaVehicle.getEmail(), Subscription.ContactType.EMAIL))
                .setVrm(cancelledSubscriptionItemForDvlaVehicle.getVrm())
                .setMotIdentification(motIdentification)
                .setReasonForCancellation(REASON_FOR_CANCELLATION_USER_CANCELLED);

        repository.save(cancelledSubscriptionForDvlaVehicle);

        String cancelledSubscriptionEmail = cancelledSubscriptionForDvlaVehicle.getContactDetail().getValue();
        ValueMap specValueMap = new ValueMap()
                .withString(":id", cancelledSubscriptionForDvlaVehicle.getUnsubscribeId())
                .withString(":email", cancelledSubscriptionEmail);
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("id = :id AND email = :email")
                .withValueMap(specValueMap);

        Item savedItem = new DynamoDB(client()).getTable(cancelledSubscriptionTableName()).query(spec).iterator().next();

        assertNotNull("cancelled_at cannot be null when saving db", savedItem.getString("cancelled_at"));

        assertEquals("Returned item does not match", cancelledSubscriptionEmail, savedItem.getString("email"));
    }
}
