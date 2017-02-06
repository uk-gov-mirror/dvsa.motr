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
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.subscriptionTableName;

public class DynamoDbSubscriptionRepositoryTest {

    DynamoDbFixture fixture;

    DynamoDbSubscriptionRepository repo;

    @Before
    public void setUp() {
        repo = new DynamoDbSubscriptionRepository(client(), subscriptionTableName());
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
    public void getByIdReturnsEmptyIfSubscriptionDoesNotExist() {
        
        assertFalse(repo.findById("ID_THAT_DOES_NOT_EXIST").isPresent());
    }
}
