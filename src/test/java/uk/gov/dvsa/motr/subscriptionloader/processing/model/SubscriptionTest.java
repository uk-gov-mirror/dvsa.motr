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
    private Subscription subscription;

    @Before
    public void setup() {

        this.subscription = new Subscription();
    }

    @Test
    public void testSetandGetId() {

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
    public void testSetandGetVrm() {

        Subscription returnedSub = this.subscription.setVrm(TEST_VRM);

        assertEquals(TEST_VRM, this.subscription.getVrm());
        assertThat(returnedSub, instanceOf(Subscription.class));
    }

    @Test
    public void testSetandGetEmail() {

        Subscription returnedSub = this.subscription.setEmail(TEST_EMAIL);

        assertEquals(TEST_EMAIL, this.subscription.getEmail());
        assertThat(returnedSub, instanceOf(Subscription.class));
    }

    @Test
    public void testToString() {

        LocalDate now = LocalDate.now();
        Subscription subscription = this.subscription.setId(TEST_ID)
                .setMotDueDate(now)
                .setVrm(TEST_VRM)
                .setEmail(TEST_EMAIL)
                .setLoadedOnDate(now);

        assertEquals("Subscription{id='testId', motDueDate=" + now.toString() +
                ", vrm='test_vrm', email='test_email', loadedOnDate=" + now.toString() + "}", subscription.toString());
    }
}
