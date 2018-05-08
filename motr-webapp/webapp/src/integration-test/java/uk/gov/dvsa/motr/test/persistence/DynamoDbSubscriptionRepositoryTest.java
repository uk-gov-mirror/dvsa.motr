package uk.gov.dvsa.motr.test.persistence;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.test.data.RandomDataUtil;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionTable;
import uk.gov.dvsa.motr.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.component.subscription.model.ContactDetail;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.DynamoDbSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.client;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.region;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.subscriptionTableName;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.waitUntilPresent;

import static java.time.format.DateTimeFormatter.ofPattern;

public class DynamoDbSubscriptionRepositoryTest {

    SubscriptionRepository repo;
    DynamoDbFixture fixture;

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
                () -> repo.findByUnsubscribeId(expectedSubscription.getUnsubscribeId()),
                true,
                5000
        ).get();

        assertEquals(actualSubscription.getContactDetail().getValue(), expectedSubscription.getEmail());
        assertEquals(actualSubscription.getVrm(), expectedSubscription.getVrm());
        assertEquals(actualSubscription.getMotDueDate(), expectedSubscription.getMotDueDate());
        assertEquals(actualSubscription.getMotIdentification().getMotTestNumber().get(), expectedSubscription.getMotTestNumber());
    }

    @Test
    public void saveSubscriptionCorrectlySavesToDb() {

        SubscriptionItem subscriptionItem = new SubscriptionItem();
        MotIdentification motIdentification = new MotIdentification(subscriptionItem.getMotTestNumber(), null);

        Subscription subscription = new Subscription();
        subscription
                .setUnsubscribeId(subscriptionItem.getUnsubscribeId())
                .setContactDetail(new ContactDetail(subscriptionItem.getEmail(), subscriptionItem.getContactType()))
                .setVrm(subscriptionItem.getVrm())
                .setVehicleType(VehicleType.MOT)
                .setMotDueDate(subscriptionItem.getMotDueDate())
                .setMotIdentification(motIdentification);

        repo.save(subscription);

        Subscription actualSubscription = waitUntilPresent(
                () -> repo.findByUnsubscribeId(subscription.getUnsubscribeId()),
                true,
                5000
        ).get();

        assertEquals(subscriptionItem.getEmail(), actualSubscription.getContactDetail().getValue());
        assertEquals(subscriptionItem.getVrm(), actualSubscription.getVrm());
        assertEquals(subscriptionItem.getVehicleType(), actualSubscription.getVehicleType());
        assertEquals(subscriptionItem.getMotDueDate(), actualSubscription.getMotDueDate());
        assertEquals(subscriptionItem.getMotTestNumber(), actualSubscription.getMotIdentification().getMotTestNumber().get());
    }


    @Test
    public void saveSubscriptionCorrectlySavesNonModelAttributesToDb() {

        SubscriptionItem subscriptionItem = new SubscriptionItem();
        MotIdentification motIdentification = new MotIdentification(subscriptionItem.getMotTestNumber(), null);

        Subscription subscription = new Subscription();
        subscription
                .setUnsubscribeId(subscriptionItem.getUnsubscribeId())
                .setContactDetail(new ContactDetail(subscriptionItem.getEmail(), subscriptionItem.getContactType()))
                .setVrm(subscriptionItem.getVrm())
                .setVehicleType(subscriptionItem.getVehicleType())
                .setMotDueDate(subscriptionItem.getMotDueDate())
                .setMotIdentification(motIdentification);

        repo.save(subscription);

        ValueMap specValueMap = new ValueMap()
                .withString(":vrm", subscription.getVrm())
                .withString(":email", subscription.getContactDetail().getValue());
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :email")
                .withValueMap(specValueMap);

        Item savedItem = new DynamoDB(client()).getTable(subscriptionTableName()).query(spec).iterator().next();

        assertNotNull("created_at cannot be null when saving db", savedItem.getString("created_at"));

        String dueDateMd = savedItem.getString("mot_due_date_md");
        assertEquals("due date md fragment is incorrect", dueDateMd, subscription.getMotDueDate().format(ofPattern("MM-dd")));
    }

    @Test
    public void findByVrmAndEmail_ReturnsMotSubscriptionIfItExists_ForMotVehicle() {

        SubscriptionItem expectedSubscriptionForMotVehicle = new SubscriptionItem();
        expectedSubscriptionForMotVehicle.setDvlaId(null);

        fixture.table(new SubscriptionTable().item(expectedSubscriptionForMotVehicle)).run();

        Subscription actualSubscription = repo.findByVrmAndEmail(expectedSubscriptionForMotVehicle.getVrm(),
                expectedSubscriptionForMotVehicle.getEmail()).get();

        assertEquals(actualSubscription.getContactDetail().getValue(), expectedSubscriptionForMotVehicle.getEmail());
        assertEquals(actualSubscription.getVrm(), expectedSubscriptionForMotVehicle.getVrm());
        assertEquals(actualSubscription.getMotDueDate(), expectedSubscriptionForMotVehicle.getMotDueDate());
        assertEquals(actualSubscription
                .getMotIdentification().getMotTestNumber().get(), expectedSubscriptionForMotVehicle.getMotTestNumber());
    }

    @Test
    public void findByVrmAndEmail_ReturnsMotSubscriptionIfItExists_ForDvlaVehicle() {

        SubscriptionItem expectedSubscriptionForDvlaVehicle = new SubscriptionItem();
        expectedSubscriptionForDvlaVehicle.setMotTestNumber(null);

        fixture.table(new SubscriptionTable().item(expectedSubscriptionForDvlaVehicle)).run();

        Subscription actualSubscription = repo.findByVrmAndEmail(expectedSubscriptionForDvlaVehicle.getVrm(),
                expectedSubscriptionForDvlaVehicle.getEmail()).get();

        assertEquals(actualSubscription.getContactDetail().getValue(), expectedSubscriptionForDvlaVehicle.getEmail());
        assertEquals(actualSubscription.getVrm(), expectedSubscriptionForDvlaVehicle.getVrm());
        assertEquals(actualSubscription.getMotDueDate(), expectedSubscriptionForDvlaVehicle.getMotDueDate());
        assertEquals(actualSubscription.getMotIdentification().getDvlaId().get(), expectedSubscriptionForDvlaVehicle.getDvlaId());
    }

    @Test
    public void findByVrmAndEmail_ReturnsMotSubscriptionIfItExists_WhenEmailIsAPhoneNumber() {

        SubscriptionItem expectedSubscriptionForMotVehicle = new SubscriptionItem();
        expectedSubscriptionForMotVehicle.setDvlaId(null);
        expectedSubscriptionForMotVehicle.setEmail(RandomDataUtil.phoneNumber());
        expectedSubscriptionForMotVehicle.setContactType(Subscription.ContactType.MOBILE);

        fixture.table(new SubscriptionTable().item(expectedSubscriptionForMotVehicle)).run();

        Subscription actualSubscription = repo.findByVrmAndEmail(expectedSubscriptionForMotVehicle.getVrm(),
                expectedSubscriptionForMotVehicle.getEmail()).get();

        ContactDetail actualContactDetail = actualSubscription.getContactDetail();

        assertEquals(actualContactDetail.getValue(), expectedSubscriptionForMotVehicle.getEmail());
        assertEquals(actualSubscription.getVrm(), expectedSubscriptionForMotVehicle.getVrm());
        assertEquals(actualSubscription.getMotDueDate(), expectedSubscriptionForMotVehicle.getMotDueDate());
        assertEquals(actualContactDetail.getContactType(), expectedSubscriptionForMotVehicle.getContactType());
        assertEquals(actualSubscription.getMotIdentification().getMotTestNumber().get(),
                expectedSubscriptionForMotVehicle.getMotTestNumber());
    }

    @Test
    public void findByVrmAndEmailReturnsEmptyIfSubscriptionDoesNotExist() {

        assertFalse(repo.findByVrmAndEmail("VRM_THAT_DOES_NOT_EXIST", "EMAIL_THAT_DOES_NOT_EXIST").isPresent());
    }

    @Test
    public void getByIdReturnsEmptyIfSubscriptionDoesNotExist() {

        assertFalse(repo.findByUnsubscribeId("ID_THAT_DOES_NOT_EXIST").isPresent());
    }

    @Test
    public void subscriptionIsDeleted() {

        SubscriptionItem sub = new SubscriptionItem();
        fixture.table(new SubscriptionTable().item(sub)).run();

        MotIdentification motIdentification = new MotIdentification(sub.getMotTestNumber(), null);

        Subscription subscription = new Subscription();
        subscription
                .setUnsubscribeId(sub.getUnsubscribeId())
                .setContactDetail(new ContactDetail(sub.getEmail(), Subscription.ContactType.EMAIL))
                .setVrm(sub.getVrm())
                .setMotIdentification(motIdentification);

        repo.delete(subscription);

        waitUntilPresent(() -> repo.findByUnsubscribeId(sub.getUnsubscribeId()), false, 5000);
    }
}
