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
        repo = new DynamoDbSubscriptionRepository(subscriptionTableName(), region(), true);
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

        assertEquals(expectedSubscription.getEmail(), actualSubscription.getContactDetail().getValue());
        assertEquals(expectedSubscription.getVrm(), actualSubscription.getVrm());
        assertEquals(expectedSubscription.getMotDueDate(), actualSubscription.getMotDueDate());
        assertEquals(expectedSubscription.getMotTestNumber(), actualSubscription.getMotIdentification().getMotTestNumber().get());
        assertEquals(expectedSubscription.getVehicleType(), actualSubscription.getVehicleType());
    }

    @Test
    public void saveSubscriptionCorrectlySavesToDb() {

        SubscriptionItem subscriptionItem = new SubscriptionItem();
        MotIdentification motIdentification = new MotIdentification(subscriptionItem.getMotTestNumber(), subscriptionItem.getDvlaId());

        Subscription subscription = new Subscription();
        subscription
                .setUnsubscribeId(subscriptionItem.getUnsubscribeId())
                .setContactDetail(new ContactDetail(subscriptionItem.getEmail(), subscriptionItem.getContactType()))
                .setVrm(subscriptionItem.getVrm())
                .setVehicleType(subscriptionItem.getVehicleType())
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
        assertEquals(subscriptionItem.getDvlaId(), actualSubscription.getMotIdentification().getDvlaId().get());
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

        assertEquals(expectedSubscriptionForMotVehicle.getEmail(), actualSubscription.getContactDetail().getValue());
        assertEquals(expectedSubscriptionForMotVehicle.getVrm(), actualSubscription.getVrm());
        assertEquals(expectedSubscriptionForMotVehicle.getMotDueDate(), actualSubscription.getMotDueDate());
        assertEquals(expectedSubscriptionForMotVehicle.getVehicleType(), actualSubscription.getVehicleType());
        assertEquals(expectedSubscriptionForMotVehicle.getMotTestNumber(),
                actualSubscription.getMotIdentification().getMotTestNumber().get());
    }

    @Test
    public void findByVrmAndEmail_ReturnsMotSubscriptionIfItExists_ForDvlaVehicle() {

        SubscriptionItem expectedSubscriptionForDvlaVehicle = new SubscriptionItem();
        expectedSubscriptionForDvlaVehicle.setMotTestNumber(null);

        fixture.table(new SubscriptionTable().item(expectedSubscriptionForDvlaVehicle)).run();

        Subscription actualSubscription = repo.findByVrmAndEmail(expectedSubscriptionForDvlaVehicle.getVrm(),
                expectedSubscriptionForDvlaVehicle.getEmail()).get();

        assertEquals(expectedSubscriptionForDvlaVehicle.getEmail(), actualSubscription.getContactDetail().getValue());
        assertEquals(expectedSubscriptionForDvlaVehicle.getVrm(), actualSubscription.getVrm());
        assertEquals(expectedSubscriptionForDvlaVehicle.getMotDueDate(), actualSubscription.getMotDueDate());
        assertEquals(expectedSubscriptionForDvlaVehicle.getDvlaId(), actualSubscription.getMotIdentification().getDvlaId().get());
        assertEquals(expectedSubscriptionForDvlaVehicle.getVehicleType(), actualSubscription.getVehicleType());
    }

    @Test
    public void findByVrmAndEmail_ReturnsMotSubscriptionIfItExists_ForHgvVehicle() {

        SubscriptionItem expectedSubscriptionForHgvVehicle = new SubscriptionItem();
        expectedSubscriptionForHgvVehicle.setVehicleType(VehicleType.HGV);

        fixture.table(new SubscriptionTable().item(expectedSubscriptionForHgvVehicle)).run();

        Subscription actualSubscription = repo.findByVrmAndEmail(expectedSubscriptionForHgvVehicle.getVrm(),
                expectedSubscriptionForHgvVehicle.getEmail()).get();

        assertEquals(expectedSubscriptionForHgvVehicle.getEmail(), actualSubscription.getContactDetail().getValue());
        assertEquals(expectedSubscriptionForHgvVehicle.getVrm(), actualSubscription.getVrm());
        assertEquals(expectedSubscriptionForHgvVehicle.getMotDueDate(), actualSubscription.getMotDueDate());
        assertEquals(expectedSubscriptionForHgvVehicle.getDvlaId(), actualSubscription.getMotIdentification().getDvlaId().get());
        assertEquals(expectedSubscriptionForHgvVehicle.getMotTestNumber(),
                actualSubscription.getMotIdentification().getMotTestNumber().get());
        assertEquals(expectedSubscriptionForHgvVehicle.getVehicleType(), actualSubscription.getVehicleType());
    }

    @Test
    public void findByVrmAndEmail_ReturnsMotSubscriptionIfItExists_ForPsvVehicle() {

        SubscriptionItem expectedSubscriptionForPsvVehicle = new SubscriptionItem();
        expectedSubscriptionForPsvVehicle.setVehicleType(VehicleType.PSV);

        fixture.table(new SubscriptionTable().item(expectedSubscriptionForPsvVehicle)).run();

        Subscription actualSubscription = repo.findByVrmAndEmail(expectedSubscriptionForPsvVehicle.getVrm(),
                expectedSubscriptionForPsvVehicle.getEmail()).get();

        assertEquals(expectedSubscriptionForPsvVehicle.getEmail(), actualSubscription.getContactDetail().getValue());
        assertEquals(expectedSubscriptionForPsvVehicle.getVrm(), actualSubscription.getVrm());
        assertEquals(expectedSubscriptionForPsvVehicle.getMotDueDate(), actualSubscription.getMotDueDate());
        assertEquals(expectedSubscriptionForPsvVehicle.getDvlaId(), actualSubscription.getMotIdentification().getDvlaId().get());
        assertEquals(expectedSubscriptionForPsvVehicle.getMotTestNumber(),
                actualSubscription.getMotIdentification().getMotTestNumber().get());
        assertEquals(expectedSubscriptionForPsvVehicle.getVehicleType(), actualSubscription.getVehicleType());
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

        assertEquals(expectedSubscriptionForMotVehicle.getEmail(), actualContactDetail.getValue());
        assertEquals(expectedSubscriptionForMotVehicle.getVrm(), actualSubscription.getVrm());
        assertEquals(expectedSubscriptionForMotVehicle.getVehicleType(), actualSubscription.getVehicleType());
        assertEquals(expectedSubscriptionForMotVehicle.getMotDueDate(), actualSubscription.getMotDueDate());
        assertEquals(expectedSubscriptionForMotVehicle.getContactType(), actualContactDetail.getContactType());
        assertEquals(expectedSubscriptionForMotVehicle.getMotTestNumber(),
                actualSubscription.getMotIdentification().getMotTestNumber().get());
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
                .setVehicleType(sub.getVehicleType())
                .setMotIdentification(motIdentification);

        repo.delete(subscription);

        waitUntilPresent(() -> repo.findByUnsubscribeId(sub.getUnsubscribeId()), false, 5000);
    }
}
