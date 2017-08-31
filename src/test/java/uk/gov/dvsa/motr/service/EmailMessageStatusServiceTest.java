package uk.gov.dvsa.motr.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailMessageStatusServiceTest {

    private static final String EMAIL_MESSAGE_TYPE = "email";
    private static final String TEST_PERMANENT_EMAIL = "test@permanent.org";
    private static final String TEST_TEMPORARY_EMAIL = "test@temporary.org";
    private static final String TEST_TECHNICAL_EMAIL = "test@technical.org";

    private NotificationClient notificationClient = mock(NotificationClient.class);
    private NotificationList notificationList = mock(NotificationList.class);
    private List<Notification> listOfNotifications;
    private Notification notificationOfPermanentFailures = mock(Notification.class);
    private Notification notificationOfTemporaryFailures = mock(Notification.class);
    private Notification notificationOfTechnicalFailures = mock(Notification.class);

    private EmailMessageStatusService emailMessageStatusService;
    private DateTime dateFilter;
    private List<String> result;

    @Before
    public void setUp() throws NotificationClientException {

        dateFilter = DateTime.now();
        System.out.print(dateFilter.toLocalDate());

        when(notificationOfPermanentFailures.getEmailAddress()).thenReturn(Optional.of(TEST_PERMANENT_EMAIL));
        when(notificationOfTemporaryFailures.getEmailAddress()).thenReturn(Optional.of(TEST_TEMPORARY_EMAIL));
        when(notificationOfTechnicalFailures.getEmailAddress()).thenReturn(Optional.of(TEST_TECHNICAL_EMAIL));

        when(notificationOfPermanentFailures.getCreatedAt()).thenReturn(dateFilter.minusDays(1));
        when(notificationOfTemporaryFailures.getCreatedAt()).thenReturn(dateFilter.minusDays(1));
        when(notificationOfTechnicalFailures.getCreatedAt()).thenReturn(dateFilter.minusDays(1));

        when(notificationOfPermanentFailures.getStatus()).thenReturn(EmailMessageStatusService.PERMANENT_FAILURE_MESSAGE_STATUS);
        when(notificationOfTemporaryFailures.getStatus()).thenReturn(EmailMessageStatusService.TEMPORARY_FAILURE_MESSAGE_STATUS);
        when(notificationOfTechnicalFailures.getStatus()).thenReturn(EmailMessageStatusService.TECHNICAL_FAILURE_MESSAGE_STATUS);

        when(notificationList.getNextPageLink()).thenReturn(Optional.ofNullable(null));

        emailMessageStatusService = new EmailMessageStatusService(notificationClient);
    }

    @Test
    public void testGetPermanentlyFailingNotifications() throws NotificationClientException {

        listOfNotifications = Arrays.asList(
                notificationOfPermanentFailures, notificationOfPermanentFailures, notificationOfPermanentFailures
        );

        when(notificationClient.getNotifications(any(), any(), any(), any())).thenReturn(notificationList);
        when(notificationList.getNotifications()).thenReturn(listOfNotifications);

        result = emailMessageStatusService.getEmailAddressesAssociatedWithNotifications(
                EmailMessageStatusService.PERMANENT_FAILURE_MESSAGE_STATUS, dateFilter);

        assertEquals("size is not equal to 3", 3, result.size());

        verify(notificationClient, times(1)).getNotifications(
                EmailMessageStatusService.PERMANENT_FAILURE_MESSAGE_STATUS,
                EMAIL_MESSAGE_TYPE,
                null,
                null
        );
    }

    @Test
    public void testGetTemporaryFailingNotifications() throws NotificationClientException {

        listOfNotifications = Arrays.asList(
                notificationOfTemporaryFailures, notificationOfTemporaryFailures
        );

        when(notificationClient.getNotifications(any(), any(), any(), any())).thenReturn(notificationList);
        when(notificationList.getNotifications()).thenReturn(listOfNotifications);

        result = emailMessageStatusService.getEmailAddressesAssociatedWithNotifications(
                EmailMessageStatusService.TEMPORARY_FAILURE_MESSAGE_STATUS, dateFilter);

        assertEquals("size is not equal to 2", 2, result.size());

        verify(notificationClient, times(1)).getNotifications(
                EmailMessageStatusService.TEMPORARY_FAILURE_MESSAGE_STATUS,
                EMAIL_MESSAGE_TYPE,
                null,
                null
        );
    }

    @Test
    public void testGetTechnicalFailingNotifications() throws NotificationClientException {

        listOfNotifications = Arrays.asList();

        when(notificationClient.getNotifications(any(), any(), any(), any())).thenReturn(notificationList);
        when(notificationList.getNotifications()).thenReturn(listOfNotifications);

        result = emailMessageStatusService.getEmailAddressesAssociatedWithNotifications(
                EmailMessageStatusService.TECHNICAL_FAILURE_MESSAGE_STATUS, dateFilter);

        assertEquals("size is not equal to 0", 0, result.size());

        verify(notificationClient, times(1)).getNotifications(
                EmailMessageStatusService.TECHNICAL_FAILURE_MESSAGE_STATUS,
                EMAIL_MESSAGE_TYPE,
                null,
                null
        );
    }

    @Test
    public void testGetPaginatedNotifications() throws NotificationClientException {

        when(notificationList.getNextPageLink()).thenReturn(Optional.ofNullable("next"), Optional.ofNullable(null));

        listOfNotifications = new ArrayList<>();

        for (int i = 0; i < 400; i++) {
            listOfNotifications.add(notificationOfPermanentFailures);
        }

        when(notificationClient.getNotifications(any(), any(), any(), any())).thenReturn(notificationList);

        when(notificationList.getNotifications()).thenReturn(
                listOfNotifications.subList(0,250),
                listOfNotifications.subList(0,250),
                listOfNotifications.subList(250,400)
        );

        result = emailMessageStatusService.getEmailAddressesAssociatedWithNotifications(
                EmailMessageStatusService.PERMANENT_FAILURE_MESSAGE_STATUS, dateFilter);

        assertEquals("size is not equal to 400", 400, result.size());

        verify(notificationClient, times(2)).getNotifications(
                EmailMessageStatusService.PERMANENT_FAILURE_MESSAGE_STATUS,
                EMAIL_MESSAGE_TYPE,
                null,
                null
        );
    }
}
