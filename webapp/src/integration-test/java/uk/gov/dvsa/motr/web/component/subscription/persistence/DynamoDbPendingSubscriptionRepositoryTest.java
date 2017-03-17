package uk.gov.dvsa.motr.web.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.PendingSubscriptionItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.PendingSubscriptionTable;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.client;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.pendingSubscriptionTableName;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.region;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.waitUntilPresent;

import static java.time.format.DateTimeFormatter.ofPattern;

public class DynamoDbPendingSubscriptionRepositoryTest {

    PendingSubscriptionRepository repo;
    DynamoDbFixture fixture;

    @Before
    public void setUp() {
        repo = new DynamoDbPendingSubscriptionRepository(pendingSubscriptionTableName(), region());
        fixture = new DynamoDbFixture(client());
    }

    @Test
    public void getByIdReturnsSubscriptionIfExistsInDb() {

        PendingSubscriptionItem expectedSubscription = new PendingSubscriptionItem();

        fixture.table(new PendingSubscriptionTable().item(expectedSubscription)).run();

        PendingSubscription actualSubscription = waitUntilPresent(
                () -> repo.findById(expectedSubscription.getId()),
                true,
                5000
        ).get();

        assertEquals(actualSubscription.getEmail(), expectedSubscription.getEmail());
        assertEquals(actualSubscription.getVrm(), expectedSubscription.getVrm());
        assertEquals(actualSubscription.getMotDueDate(), expectedSubscription.getMotDueDate());
    }

    @Test
    public void saveSubscriptionCorrectlySavesToDb() {

        PendingSubscriptionItem subscriptionItem = new PendingSubscriptionItem();

        PendingSubscription subscription = new PendingSubscription(subscriptionItem.getId());
        subscription
                .setEmail(subscriptionItem.getEmail())
                .setVrm(subscriptionItem.getVrm())
                .setMotDueDate(subscriptionItem.getMotDueDate());

        repo.save(subscription);

        PendingSubscription actualSubscription = waitUntilPresent(
                () -> repo.findById(subscription.getId()),
                true,
                5000
        ).get();

        assertEquals(subscriptionItem.getEmail(), actualSubscription.getEmail());
        assertEquals(subscriptionItem.getVrm(), actualSubscription.getVrm());
        assertEquals(subscriptionItem.getMotDueDate(), actualSubscription.getMotDueDate());
    }


    @Test
    public void saveSubscriptionCorrectlySavesNonModelAttributesToDb() {

        PendingSubscriptionItem subscriptionItem = new PendingSubscriptionItem();

        PendingSubscription subscription = new PendingSubscription(subscriptionItem.getId());
        subscription
                .setEmail(subscriptionItem.getEmail())
                .setVrm(subscriptionItem.getVrm())
                .setMotDueDate(subscriptionItem.getMotDueDate());

        repo.save(subscription);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :email")
                .withValueMap(new ValueMap().withString(":vrm", subscription.getVrm()).withString(":email", subscription.getEmail()));

        Item savedItem = new DynamoDB(client()).getTable(pendingSubscriptionTableName()).query(spec).iterator().next();

        assertNotNull("created_at cannot be null when saving db", savedItem.getString("created_at"));

        String dueDateMd = savedItem.getString("mot_due_date_md");
        assertEquals("due date md fragment is incorrect", dueDateMd, subscription.getMotDueDate().format(ofPattern("MM-dd")));
    }

    @Test
    public void getByIdReturnsEmptyIfSubscriptionDoesNotExist() {

        assertFalse(repo.findById("ID_THAT_DOES_NOT_EXIST").isPresent());
    }

    @Test
    public void subscriptionIsDeleted() {

        PendingSubscriptionItem sub = new PendingSubscriptionItem();
        fixture.table(new PendingSubscriptionTable().item(sub)).run();

        PendingSubscription subscription = new PendingSubscription(sub.getId());
        subscription
                .setEmail(sub.getEmail())
                .setVrm(sub.getVrm());

        repo.delete(subscription);

        waitUntilPresent(() -> repo.findById(sub.getId()), false, 5000);
    }
}
