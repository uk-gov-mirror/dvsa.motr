package uk.gov.dvsa.motr.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.report.StatusReport;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SendStatusReportServiceTest {

    private static final String STATUS_REPORT_EMAIL_RECIPIENT_1 = "test1@test.com";
    private static final String STATUS_REPORT_EMAIL_RECIPIENT_2 = "test2@test.com";

    private NotifyService notifyService = mock(NotifyService.class);
    private SendStatusReportService sendStatusReportService;
    private List<String> emailRecipients;

    @Before
    public void setUp() throws NotificationClientException {

        this.emailRecipients = Arrays.asList(STATUS_REPORT_EMAIL_RECIPIENT_1, STATUS_REPORT_EMAIL_RECIPIENT_2);

        this.sendStatusReportService = new SendStatusReportService(this.notifyService, this.emailRecipients);
    }

    @Test
    public void testSendStatusReportServiceRecipients() throws NotificationClientException {

        HashMap<String, List<Notification>> emails = new HashMap<>();

        Notification notification = mock(Notification.class);
        when(notification.getNotificationType()).thenReturn(NotificationStatusService.NOTIFICATION_TYPE_EMAIL);
        when(notification.getEmailAddress()).thenReturn(Optional.of("email@email.com"));

        emails.put(NotificationStatusService.PERMANENT_FAILURE, Arrays.asList(notification));
        emails.put(NotificationStatusService.TEMPORARY_FAILURE, Arrays.asList(notification));
        emails.put(NotificationStatusService.TECHNICAL_FAILURE, Arrays.asList(notification));

        StatusReport statusReport = new StatusReport(emails, DateTime.now());

        this.sendStatusReportService.sendNotifications(statusReport);

        verify(this.notifyService, times(1)).sendStatusEmail(
                STATUS_REPORT_EMAIL_RECIPIENT_1,
                statusReport
        );

        verify(this.notifyService, times(1)).sendStatusEmail(
                STATUS_REPORT_EMAIL_RECIPIENT_2,
                statusReport
        );

        verify(this.notifyService, times(2)).sendStatusEmail(any(), any());
    }
}
