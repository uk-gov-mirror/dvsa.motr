package uk.gov.dvsa.motr.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.report.StatusReport;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotifyServiceTest {

    private static final String STATUS_REPORT_EMAIL_TEMPLATE_ID = "53919b96-8e49-11e7-bb31-be2e44b06b34";
    private static final String STATUS_REPORT_EMAIL_ADDRESS = "test@test.com";
    private static final String PERMANENT_FAILURE_EMAIL_ADDRESS = "permfail@test.com";
    private static final String TEMPORARY_FAILURE_EMAIL_ADDRESS = "tempfail@test.com";
    private static final String TECHNICAL_FAILURE_EMAIL_ADDRESS = "techfail@test.com";

    private NotificationClient notificationClient = mock(NotificationClient.class);
    private NotifyService notifyService;

    @Before
    public void setUp() throws NotificationClientException {

        this.notifyService = new NotifyService(notificationClient, STATUS_REPORT_EMAIL_TEMPLATE_ID);
    }

    @Test
    public void testSendingOfStatusReportEmail() throws NotificationClientException {

        HashMap<String, List<Notification>> notifications = new HashMap<>();
        Notification notification = mock(Notification.class);
        when(notification.getNotificationType()).thenReturn(NotificationStatusService.NOTIFICATION_TYPE_EMAIL);
        when(notification.getEmailAddress()).thenReturn(Optional.of("email@email.com"));

        notifications.put(NotificationStatusService.PERMANENT_FAILURE, Arrays.asList(notification));
        notifications.put(NotificationStatusService.TEMPORARY_FAILURE, Arrays.asList(notification));
        notifications.put(NotificationStatusService.TECHNICAL_FAILURE, Arrays.asList(notification));

        StatusReport statusReport = new StatusReport(notifications, DateTime.now());

        Map<String, String> statistics = new HashMap<>();
        statistics.put("temporary_failures", Integer.toString(statusReport.getTemporaryFailedNotifications()) + "(email) 0(SMS) ");
        statistics.put("technical_failures", Integer.toString(statusReport.getTechnicalFailedNotifications()) + "(email) 0(SMS) ");
        statistics.put("permanent_failures", Integer.toString(statusReport.getPermanentlyFailedNotifications()) + "(email) 0(SMS) ");
        statistics.put("date", statusReport.getDate().minusDays(1).toLocalDate().toString());

        notifyService.sendStatusEmail(STATUS_REPORT_EMAIL_ADDRESS, statusReport);

        verify(notificationClient, times(1)).sendEmail(
                STATUS_REPORT_EMAIL_TEMPLATE_ID,
                STATUS_REPORT_EMAIL_ADDRESS,
                statistics,
                ""
        );
    }
}
