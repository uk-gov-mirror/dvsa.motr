package uk.gov.dvsa.motr.web.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionTable;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.client;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.region;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.subscriptionTableName;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Optional.empty;

public class DynamoDbSubscriptionRepositoryTest {

    DynamoDbFixture fixture;

    DynamoDbSubscriptionRepository repo;

    @Before
    public void setUp() {
        repo = new DynamoDbSubscriptionRepository(subscriptionTableName(), region());
        fixture = new DynamoDbFixture(client());
    }

    @Test
    public void getByIdReturnsSubscriptionIfExistsInDb() {

        SubscriptionItem expectedSubscription = new SubscriptionItem();

        fixture.table(new SubscriptionTable().item(expectedSubscription)).run();

        Subscription actualSubscription = waitUntilPresent(
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

        SubscriptionItem subscriptionItem = new SubscriptionItem();

        Subscription subscription = new Subscription(subscriptionItem.getId())
                .setEmail(subscriptionItem.getEmail())
                .setVrm(subscriptionItem.getVrm())
                .setMotDueDate(subscriptionItem.getMotDueDate());

        repo.save(subscription);

        Subscription actualSubscription = waitUntilPresent(
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

        SubscriptionItem subscriptionItem = new SubscriptionItem();

        Subscription subscription = new Subscription(subscriptionItem.getId())
                .setEmail(subscriptionItem.getEmail())
                .setVrm(subscriptionItem.getVrm())
                .setMotDueDate(subscriptionItem.getMotDueDate());

        repo.save(subscription);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :email")
                .withValueMap(new ValueMap().withString(":vrm", subscription.getVrm()).withString(":email", subscription.getEmail()));

        Item savedItem = new DynamoDB(client()).getTable(subscriptionTableName()).query(spec).iterator().next();

        assertNotNull("created_at cannot be null when saving db", savedItem.getString("created_at"));

        String dueDateMd = savedItem.getString("mot_due_date_md");
        assertEquals("due date md fragment is incorrect", dueDateMd, subscription.getMotDueDate().format(ofPattern("MM-dd")));
    }


    @Test
    public void findByVrmAndEmailReturnsSubscriptionIfItExists() {

        SubscriptionItem expectedSubscription = new SubscriptionItem();

        fixture.table(new SubscriptionTable().item(expectedSubscription)).run();

        Subscription actualSubscription = repo.findByVrmAndEmail(expectedSubscription.getVrm(), expectedSubscription.getEmail()).get();

        assertEquals(actualSubscription.getEmail(), expectedSubscription.getEmail());
        assertEquals(actualSubscription.getVrm(), expectedSubscription.getVrm());
        assertEquals(actualSubscription.getMotDueDate(), expectedSubscription.getMotDueDate());
    }

    @Test
    public void findByVrmAndEmailReturnsEmptyIfSubscriptionDoesNotExist() {

        assertFalse(repo.findByVrmAndEmail("VRM_THAT_DOES_NOT_EXIST", "EMAIL_THAT_DOES_NOT_EXIST").isPresent());
    }

    @Test
    public void getByIdReturnsEmptyIfSubscriptionDoesNotExist() {

        assertFalse(repo.findById("ID_THAT_DOES_NOT_EXIST").isPresent());
    }

    @Test
    public void subscriptionIsDeleted() {

        SubscriptionItem sub = new SubscriptionItem();
        fixture.table(new SubscriptionTable().item(sub)).run();

        Subscription subscription = new Subscription(sub.getId())
                .setEmail(sub.getEmail())
                .setVrm(sub.getVrm());

        repo.delete(subscription);

        waitUntilPresent(() -> repo.findById(sub.getId()), false, 5000);
    }

    /**
     * Waits till a supplier that returns Optional returns it with a certain state
     *
     * @param optionalSupplier supplier that provides the result
     * @param isPresent        asserted state of Optional
     * @param timeout          maximum time that the asserted state will be waited for
     */
    private <T> Optional<T> waitUntilPresent(Supplier<Optional<T>> optionalSupplier, boolean isPresent, long timeout) {

        long current = currentTimeMillis();
        boolean conditionSatisfied = false;
        Optional<T> optional = empty();

        while (!conditionSatisfied || (currentTimeMillis() - current) < timeout) {
            try {
                optional = optionalSupplier.get();
                conditionSatisfied = optional.isPresent() == isPresent;
                if (conditionSatisfied) {
                    break;
                }
                sleep(100);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        assertTrue(format("Assert timed out after %s ms", timeout), conditionSatisfied);

        return optional;
    }
}
