package uk.gov.dvsa.motr.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.persistence.entity.SubscriptionDbItem;
import uk.gov.dvsa.motr.persistence.repository.CancelledSubscriptionRepository;
import uk.gov.dvsa.motr.persistence.repository.SubscriptionRepository;
import uk.gov.dvsa.motr.report.BouncingEmailCleanerReport;
import uk.gov.service.notify.NotificationClientException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnsubscribeBouncingEmailAddressServiceTest {

    private SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private CancelledSubscriptionRepository cancelledSubscriptionRepository = mock(CancelledSubscriptionRepository.class);
    private EmailMessageStatusService emailMessageStatusService = mock(EmailMessageStatusService.class);
    private SubscriptionDbItem subscriptionDbItem = mock(SubscriptionDbItem.class);

    private List<String> emailList;

    private UnsubscribeBouncingEmailAddressService unsubscribeBouncingEmailAddressService;

    @Before
    public void setUp() throws NotificationClientException {
        unsubscribeBouncingEmailAddressService = new UnsubscribeBouncingEmailAddressService(
                subscriptionRepository,
                cancelledSubscriptionRepository,
                emailMessageStatusService);

        emailList = Arrays.asList("test@example.org", "test2@example.org", "test3@example.org");
        List<SubscriptionDbItem> subscriptionDbItemList = Arrays.asList(subscriptionDbItem, subscriptionDbItem, subscriptionDbItem);
        when(emailMessageStatusService.getEmailAddressesAssociatedWithPermanentlyFailingNotifications()).thenReturn(emailList);
        when(subscriptionRepository.findByEmails(emailList)).thenReturn(subscriptionDbItemList);
    }

    @Test
    public void testUnsubscribeBouncingEmailAddresses() throws NotificationClientException {

        BouncingEmailCleanerReport result = unsubscribeBouncingEmailAddressService.run();

        verify(subscriptionRepository, times(emailList.size())).deleteRecord(any());
        verify(cancelledSubscriptionRepository, times(emailList.size())).cancelSubscription(subscriptionDbItem);

        assertEquals(3, result.getNumberOfSubscriptionsSuccessfullyCancelled());
    }
}
