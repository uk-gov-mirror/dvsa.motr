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

public class NotificationStatusServiceTest {

    private static final String MESSAGE_TYPE = null;
    private static final String TEST_PERMANENT_EMAIL = "test@permanent.org";
    private static final String TEST_TEMPORARY_EMAIL = "test@temporary.org";
    private static final String TEST_TECHNICAL_EMAIL = "test@technical.org";

    private NotificationClient notificationClient = mock(NotificationClient.class);
    private NotificationList notificationList = mock(NotificationList.class);
    private List<Notification> listOfNotifications;
    private Notification notificationOfPermanentFailure = mock(Notification.class);
    private Notification notificationOfTemporaryFailure = mock(Notification.class);
    private Notification notificationOfTechnicalFailure = mock(Notification.class);

    private NotificationStatusService notificationStatusService;
    private DateTime dateFilter;
    private List<Notification> result;

    @Before
    public void setUp() throws NotificationClientException {

        dateFilter = DateTime.now();
        System.out.print(dateFilter.toLocalDate());

        when(notificationOfPermanentFailure.getNotificationType()).thenReturn(NotificationStatusService.NOTIFICATION_TYPE_EMAIL);
        when(notificationOfTemporaryFailure.getNotificationType()).thenReturn(NotificationStatusService.NOTIFICATION_TYPE_EMAIL);
        when(notificationOfTechnicalFailure.getNotificationType()).thenReturn(NotificationStatusService.NOTIFICATION_TYPE_EMAIL);

        when(notificationOfPermanentFailure.getEmailAddress()).thenReturn(Optional.of(TEST_PERMANENT_EMAIL));
        when(notificationOfTemporaryFailure.getEmailAddress()).thenReturn(Optional.of(TEST_TEMPORARY_EMAIL));
        when(notificationOfTechnicalFailure.getEmailAddress()).thenReturn(Optional.of(TEST_TECHNICAL_EMAIL));

        when(notificationOfPermanentFailure.getCreatedAt()).thenReturn(dateFilter.minusDays(1));
        when(notificationOfTemporaryFailure.getCreatedAt()).thenReturn(dateFilter.minusDays(1));
        when(notificationOfTechnicalFailure.getCreatedAt()).thenReturn(dateFilter.minusDays(1));

        when(notificationOfPermanentFailure.getStatus()).thenReturn(NotificationStatusService.PERMANENT_FAILURE);
        when(notificationOfTemporaryFailure.getStatus()).thenReturn(NotificationStatusService.TEMPORARY_FAILURE);
        when(notificationOfTechnicalFailure.getStatus()).thenReturn(NotificationStatusService.TECHNICAL_FAILURE);

        when(notificationList.getNextPageLink()).thenReturn(Optional.ofNullable(null));

        notificationStatusService = new NotificationStatusService(notificationClient);
    }

    @Test
    public void testGetPermanentlyFailingNotifications() throws NotificationClientException {

        listOfNotifications = Arrays.asList(
                notificationOfPermanentFailure, notificationOfPermanentFailure, notificationOfPermanentFailure
        );

        when(notificationClient.getNotifications(any(), any(), any(), any())).thenReturn(notificationList);
        when(notificationList.getNotifications()).thenReturn(listOfNotifications);

        result = notificationStatusService.getFilteredNotifications(
                NotificationStatusService.PERMANENT_FAILURE, dateFilter);

        assertEquals("size is not equal to 3", 3, result.size());

        verify(notificationClient, times(1)).getNotifications(
                NotificationStatusService.PERMANENT_FAILURE,
                MESSAGE_TYPE,
                null,
                null
        );
    }

    @Test
    public void testGetTemporaryFailingNotifications() throws NotificationClientException {

        listOfNotifications = Arrays.asList(
                notificationOfTemporaryFailure, notificationOfTemporaryFailure
        );

        when(notificationClient.getNotifications(any(), any(), any(), any())).thenReturn(notificationList);
        when(notificationList.getNotifications()).thenReturn(listOfNotifications);

        result = notificationStatusService.getFilteredNotifications(
                NotificationStatusService.TEMPORARY_FAILURE, dateFilter);

        assertEquals("size is not equal to 2", 2, result.size());

        verify(notificationClient, times(1)).getNotifications(
                NotificationStatusService.TEMPORARY_FAILURE,
                MESSAGE_TYPE,
                null,
                null
        );
    }

    @Test
    public void testGetTechnicalFailingNotifications() throws NotificationClientException {

        listOfNotifications = Arrays.asList();

        when(notificationClient.getNotifications(any(), any(), any(), any())).thenReturn(notificationList);
        when(notificationList.getNotifications()).thenReturn(listOfNotifications);

        result = notificationStatusService.getFilteredNotifications(
                NotificationStatusService.TECHNICAL_FAILURE, dateFilter);

        assertEquals("size is not equal to 0", 0, result.size());

        verify(notificationClient, times(1)).getNotifications(
                NotificationStatusService.TECHNICAL_FAILURE,
                MESSAGE_TYPE,
                null,
                null
        );
    }

    @Test
    public void testGetPaginatedNotifications() throws NotificationClientException {

        when(notificationList.getNextPageLink()).thenReturn(Optional.ofNullable("next"), Optional.ofNullable(null));

        listOfNotifications = new ArrayList<>();

        for (int i = 0; i < 400; i++) {
            listOfNotifications.add(notificationOfPermanentFailure);
        }

        when(notificationClient.getNotifications(any(), any(), any(), any())).thenReturn(notificationList);

        when(notificationList.getNotifications()).thenReturn(
                listOfNotifications.subList(0,250),
                listOfNotifications.subList(0,250),
                listOfNotifications.subList(250,400)
        );

        result = notificationStatusService.getFilteredNotifications(
                NotificationStatusService.PERMANENT_FAILURE, dateFilter);

        assertEquals("size is not equal to 400", 400, result.size());

        verify(notificationClient, times(2)).getNotifications(
                NotificationStatusService.PERMANENT_FAILURE,
                MESSAGE_TYPE,
                null,
                null
        );
    }
}
