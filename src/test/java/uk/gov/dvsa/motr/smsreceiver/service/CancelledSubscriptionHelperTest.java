package uk.gov.dvsa.motr.smsreceiver.service;

import com.amazonaws.services.dynamodbv2.document.Item;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.smsreceiver.events.SuccessfullyUnsubscribedEvent;
import uk.gov.dvsa.motr.smsreceiver.subscription.model.CancelledSubscription;
import uk.gov.dvsa.motr.smsreceiver.subscription.model.Subscription;
import uk.gov.dvsa.motr.smsreceiver.subscription.persistence.CancelledSubscriptionRepository;

import java.time.LocalDate;
import java.util.Iterator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EventLogger.class})
public class CancelledSubscriptionHelperTest {

    private static final String TEST_VRM = "TEST-VRM";
    private static final String MOBILE_NUMBER = "12345678";

    private static final String TEST_VRM_NOT_IN_DB = "UNKNOWN-VRM";
    private static final String MOBILE_NUMBER_NOT_IN_DB = "98765432";

    private CancelledSubscriptionRepository cancelledSubscriptionRepository = mock(CancelledSubscriptionRepository.class);
    private CancelledSubscriptionHelper cancelledSubscriptionHelper;

    @Before
    public void setup() {

        PowerMockito.mockStatic(EventLogger.class);
        cancelledSubscriptionHelper = new CancelledSubscriptionHelper(cancelledSubscriptionRepository);
    }

    @Test
    public void whenWeCallToCreateANewCancelledSubscription_ThenACancelledSubscriptionIsSaved() {

        doNothing().when(cancelledSubscriptionRepository).save(any(CancelledSubscription.class));

        Subscription subscriptionToSave = buildTestSubscription(TEST_VRM, MOBILE_NUMBER);
        cancelledSubscriptionHelper.createANewCancelledSubscriptionEntry(subscriptionToSave);

        verify(cancelledSubscriptionRepository, times(1)).save(any(CancelledSubscription.class));

        verifyStatic(times(1));
        EventLogger.logEvent(isA(SuccessfullyUnsubscribedEvent.class));
    }

    @Test
    public void whenThereIsAMatchingCancelledSubscription_TheWeGetAMatchFlagReturned() {

        Iterator<Item> testItemsFromCancelledTable = buildTestItems(TEST_VRM, MOBILE_NUMBER, true);
        when(cancelledSubscriptionRepository
                .findCancelledSubscriptionByVrmAndMobile(TEST_VRM, MOBILE_NUMBER))
                .thenReturn(testItemsFromCancelledTable);

        boolean matchFound = cancelledSubscriptionHelper.foundMatchingCancelledSubscription(TEST_VRM, MOBILE_NUMBER);
        assertTrue(matchFound);
    }

    @Test
    public void whenTheresNoMatchingVrmCancelledSubscription_TheWeGetNoMatchFoundFlagReturned() {

        Iterator<Item> mockIteratorOfCancelledItems = buildTestItems(TEST_VRM, MOBILE_NUMBER, false);
        when(cancelledSubscriptionRepository
                .findCancelledSubscriptionByVrmAndMobile(TEST_VRM_NOT_IN_DB, MOBILE_NUMBER))
                .thenReturn(mockIteratorOfCancelledItems);

        boolean matchFound = cancelledSubscriptionHelper.foundMatchingCancelledSubscription(TEST_VRM_NOT_IN_DB, MOBILE_NUMBER);
        assertFalse(matchFound);
    }

    private Subscription buildTestSubscription(String vrm, String mobileNumber) {

        Subscription testSubscription = new Subscription();
        testSubscription.setVrm(vrm);
        testSubscription.setContactDetail(mobileNumber);
        testSubscription.setMotDueDate(LocalDate.now());
        return testSubscription;
    }

    private Iterator<Item> buildTestItems(String vrm, String mobileNumber, boolean hasMatch) {

        Item matchingItem = new Item();
        matchingItem.withString("vrm", vrm);
        matchingItem.withString("email", mobileNumber);

        Iterator<Item> itr = mock(Iterator.class);
        when(itr.hasNext()).thenReturn(hasMatch);
        when(itr.next()).thenReturn(matchingItem);
        return itr;
    }
}
