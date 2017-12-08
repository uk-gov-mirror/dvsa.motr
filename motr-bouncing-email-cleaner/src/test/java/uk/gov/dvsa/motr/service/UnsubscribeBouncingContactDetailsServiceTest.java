package uk.gov.dvsa.motr.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.persistence.entity.SubscriptionDbItem;
import uk.gov.dvsa.motr.persistence.repository.CancelledSubscriptionRepository;
import uk.gov.dvsa.motr.persistence.repository.SubscriptionRepository;
import uk.gov.dvsa.motr.report.BouncingEmailCleanerReport;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnsubscribeBouncingContactDetailsServiceTest {

    private SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private CancelledSubscriptionRepository cancelledSubscriptionRepository = mock(CancelledSubscriptionRepository.class);
    private NotificationStatusService notificationStatusService = mock(NotificationStatusService.class);
    private SubscriptionDbItem subscriptionDbItem = mock(SubscriptionDbItem.class);
    private SendStatusReportService sendStatusReportService = mock(SendStatusReportService.class);
    private DateTime dateFilter;

    private UnsubscribeBouncingContactDetailsService unsubscribeBouncingContactDetailsService;

    @Before
    public void setUp() throws NotificationClientException {

        unsubscribeBouncingContactDetailsService = new UnsubscribeBouncingContactDetailsService(
                subscriptionRepository,
                cancelledSubscriptionRepository,
                notificationStatusService,
                sendStatusReportService
        );

        Notification notification = mock(Notification.class);
        when(notification.getEmailAddress()).thenReturn(Optional.of("email@email.com"));
        when(notification.getNotificationType()).thenReturn(NotificationStatusService.NOTIFICATION_TYPE_EMAIL);

        List<Notification> notifications = Arrays.asList(notification, notification, notification);

        dateFilter = DateTime.now();

        List<SubscriptionDbItem> subscriptionDbItemList = Arrays.asList(subscriptionDbItem, subscriptionDbItem, subscriptionDbItem);

        when(notificationStatusService.getFilteredNotifications(any(), eq(dateFilter))).thenReturn(notifications);

        when(subscriptionRepository.findByEmails(any())).thenReturn(subscriptionDbItemList);
    }

    @Test
    public void testUnsubscribeBouncingEmailAddresses() throws NotificationClientException {

        BouncingEmailCleanerReport result = unsubscribeBouncingContactDetailsService.run(dateFilter);

        verify(subscriptionRepository, times(3)).deleteRecord(any());
        verify(cancelledSubscriptionRepository, times(3)).cancelSubscription(subscriptionDbItem);

        assertEquals(3, result.getNumberOfSubscriptionsSuccessfullyCancelled());
    }
}
