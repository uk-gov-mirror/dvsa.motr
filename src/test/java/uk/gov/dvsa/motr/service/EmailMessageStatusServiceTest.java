package uk.gov.dvsa.motr.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailMessageStatusServiceTest {

    private static final String EMAIL_MESSAGE_TYPE = "email";
    private static final String PERMANENT_FAILURE_MESSAGE_STATUS = "permanent-failure";

    private NotificationClient notificationClient = mock(NotificationClient.class);
    private NotificationList notificationList = mock(NotificationList.class);
    private List<Notification> listOfNotifications;
    private Notification notification = mock(Notification.class);

    private EmailMessageStatusService emailMessageStatusService;

    @Before
    public void setUp() throws NotificationClientException {
        when(notification.getEmailAddress()).thenReturn(Optional.of("test@example.org"));
        listOfNotifications = Arrays.asList(notification, notification, notification);
        when(notificationClient.getNotifications(any(), any(), any(), any())).thenReturn(notificationList);
        when(notificationList.getNotifications()).thenReturn(listOfNotifications);
        emailMessageStatusService = new EmailMessageStatusService(notificationClient);
    }

    @Test
    public void testGetPermanentlyFailingNotifications() throws NotificationClientException {
        emailMessageStatusService.getEmailAddressesAssociatedWithPermanentlyFailingNotifications();

        verify(notificationClient, times(1)).getNotifications(
                PERMANENT_FAILURE_MESSAGE_STATUS,
                EMAIL_MESSAGE_TYPE,
                null,
                null
        );
    }
}
