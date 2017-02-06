package uk.gov.dvsa.motr.web.component.subscription.persistence;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionTable;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.client;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.region;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.subscriptionTableName;

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

        Subscription actualSubscription = repo.findById(expectedSubscription.getId()).get();

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

        Subscription actualSubscription = repo.findById(subscription.getId()).get();

        assertEquals(subscriptionItem.getEmail(), actualSubscription.getEmail());
        assertEquals(subscriptionItem.getVrm(), actualSubscription.getVrm());
        assertEquals(subscriptionItem.getMotDueDate(), actualSubscription.getMotDueDate());
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
}
