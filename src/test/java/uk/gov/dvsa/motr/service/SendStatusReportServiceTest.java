package uk.gov.dvsa.motr.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.report.StatusReport;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SendStatusReportServiceTest {

    private static final String STATUS_REPORT_EMAIL_RECIPIENT_1 = "test1@test.com";
    private static final String STATUS_REPORT_EMAIL_RECIPIENT_2 = "test2@test.com";
    private static final String PERMANENT_FAILURE_EMAIL_ADDRESS = "permfail@test.com";
    private static final String TEMPORARY_FAILURE_EMAIL_ADDRESS = "tempfail@test.com";
    private static final String TECHNICAL_FAILURE_EMAIL_ADDRESS = "techfail@test.com";

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

        HashMap<String, List<String>> emails = new HashMap<>();
        emails.put(EmailMessageStatusService.PERMANENT_FAILURE_MESSAGE_STATUS, Arrays.asList(PERMANENT_FAILURE_EMAIL_ADDRESS));
        emails.put(EmailMessageStatusService.TEMPORARY_FAILURE_MESSAGE_STATUS, Arrays.asList(TEMPORARY_FAILURE_EMAIL_ADDRESS));
        emails.put(EmailMessageStatusService.TECHNICAL_FAILURE_MESSAGE_STATUS, Arrays.asList(TECHNICAL_FAILURE_EMAIL_ADDRESS));

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
