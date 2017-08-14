package uk.gov.dvsa.motr.subscriptionloader.processing.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class SubscriptionTest {

    public static final String TEST_ID = "testId";
    public static final String TEST_VRM = "test_vrm";
    public static final String TEST_EMAIL = "test_email";
    public static final String TEST_MOT_TEST_NUMBER = "test_mot_test_number";
    public static final String TEST_DVLA_ID = "test_dvla_id";
    private Subscription subscription;

    @Before
    public void setup() {

        this.subscription = new Subscription();
    }

    @Test
    public void testSetAndGetId() {

        Subscription returnedSub = this.subscription.setId(TEST_ID);

        assertEquals(TEST_ID, this.subscription.getId());
        assertThat(returnedSub, instanceOf(Subscription.class));
    }

    @Test
    public void testMotDueDateSetAndGet() {

        LocalDate now = LocalDate.now();
        Subscription returnedSub = this.subscription.setMotDueDate(now);

        assertEquals(now, this.subscription.getMotDueDate());
        assertThat(returnedSub, instanceOf(Subscription.class));
    }

    @Test
    public void testSetAndGetVrm() {

        Subscription returnedSub = this.subscription.setVrm(TEST_VRM);

        assertEquals(TEST_VRM, this.subscription.getVrm());
        assertThat(returnedSub, instanceOf(Subscription.class));
    }

    @Test
    public void testSetAndGetEmail() {

        Subscription returnedSub = this.subscription.setEmail(TEST_EMAIL);

        assertEquals(TEST_EMAIL, this.subscription.getEmail());
        assertThat(returnedSub, instanceOf(Subscription.class));
    }

    @Test
    public void testSetAndGetMotTestNumber() {

        Subscription returnedSub = this.subscription.setMotTestNumber(TEST_MOT_TEST_NUMBER);

        assertEquals(TEST_MOT_TEST_NUMBER, this.subscription.getMotTestNumber());
        assertThat(returnedSub, instanceOf(Subscription.class));
    }

    @Test
    public void testSetAndGetDvlaId() {

        Subscription returnedSub = this.subscription.setDvlaId(TEST_DVLA_ID);

        assertEquals(TEST_DVLA_ID, this.subscription.getDvlaId());
        assertThat(returnedSub, instanceOf(Subscription.class));
    }

    @Test
    public void testToStringWhenMotTestNumberIsSet() {

        LocalDate now = LocalDate.now();
        Subscription subscription = this.subscription.setId(TEST_ID)
                .setMotDueDate(now)
                .setVrm(TEST_VRM)
                .setEmail(TEST_EMAIL)
                .setMotTestNumber(TEST_MOT_TEST_NUMBER)
                .setLoadedOnDate(now);

        assertEquals("Subscription{id='testId', motDueDate=" +
                now.toString() +
                ", vrm='test_vrm', email='test_email', motTestNumber='test_mot_test_number', dvlaId='null', loadedOnDate=" +
                now.toString() + "}", subscription.toString());
    }

    @Test
    public void testToStringWhenDvlaIdIsSet() {

        LocalDate now = LocalDate.now();
        Subscription subscription = this.subscription.setId(TEST_ID)
                .setMotDueDate(now)
                .setVrm(TEST_VRM)
                .setEmail(TEST_EMAIL)
                .setDvlaId(TEST_DVLA_ID)
                .setLoadedOnDate(now);

        assertEquals("Subscription{id='testId', motDueDate=" +
                now.toString() +
                ", vrm='test_vrm', email='test_email', motTestNumber='null', dvlaId='test_dvla_id', loadedOnDate=" +
                now.toString() + "}", subscription.toString());
    }
}
