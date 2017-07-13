package uk.gov.dvsa.motr.web.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.CancelledSubscriptionItem;
import uk.gov.dvsa.motr.web.component.subscription.model.CancelledSubscription;
import uk.gov.dvsa.motr.web.component.subscription.service.UnsubscribeService;

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
    public void saveCancelledSubscriptionCorrectlySavesNonModelAttributesToDb() {

        CancelledSubscriptionItem cancelledSubscriptionItem = new CancelledSubscriptionItem();

        CancelledSubscription cancelledSubscription = new CancelledSubscription();
        cancelledSubscription
                .setUnsubscribeId(cancelledSubscriptionItem.getUnsubscribeId())
                .setEmail(cancelledSubscriptionItem.getEmail())
                .setVrm(cancelledSubscriptionItem.getVrm())
                .setMotTestNumber(cancelledSubscriptionItem.getMotTestNumber())
                .setReasonForCancellation(REASON_FOR_CANCELLATION_USER_CANCELLED);

        repository.save(cancelledSubscription);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("id = :id AND email = :email")
                .withValueMap(new ValueMap().withString(":id", cancelledSubscription.getUnsubscribeId()).withString(":email",
                cancelledSubscription.getEmail()));

        Item savedItem = new DynamoDB(client()).getTable(cancelledSubscriptionTableName()).query(spec).iterator().next();

        assertNotNull("cancelled_at cannot be null when saving db", savedItem.getString("cancelled_at"));

        assertEquals("Returned item does not match", cancelledSubscription.getEmail(), savedItem.getString("email"));
    }
}
