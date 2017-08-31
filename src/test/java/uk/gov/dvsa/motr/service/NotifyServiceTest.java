package uk.gov.dvsa.motr.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.report.StatusReport;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

        HashMap<String, List<String>> emails = new HashMap<>();
        emails.put(EmailMessageStatusService.PERMANENT_FAILURE_MESSAGE_STATUS, Arrays.asList(PERMANENT_FAILURE_EMAIL_ADDRESS));
        emails.put(EmailMessageStatusService.TEMPORARY_FAILURE_MESSAGE_STATUS, Arrays.asList(TEMPORARY_FAILURE_EMAIL_ADDRESS));
        emails.put(EmailMessageStatusService.TECHNICAL_FAILURE_MESSAGE_STATUS, Arrays.asList(TECHNICAL_FAILURE_EMAIL_ADDRESS));

        StatusReport statusReport = new StatusReport(emails, DateTime.now());

        Map<String, String> statistics = new HashMap<>();
        statistics.put("temporary_failures", Integer.toString(statusReport.getTemporaryFailedNotifications()));
        statistics.put("technical_failures", Integer.toString(statusReport.getTechnicalFailedNotifications()));
        statistics.put("permanent_failures", Integer.toString(statusReport.getPermanentlyFailedNotifications()));
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
