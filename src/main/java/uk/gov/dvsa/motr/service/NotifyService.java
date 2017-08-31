package uk.gov.dvsa.motr.service;

import uk.gov.dvsa.motr.report.StatusReport;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class NotifyService {

    private NotificationClient notificationClient;
    private String statusReportEmailTemplateId;

    public NotifyService(NotificationClient notificationClient, String statusReportEmailTemplateId) {

        this.notificationClient = notificationClient;
        this.statusReportEmailTemplateId = statusReportEmailTemplateId;
    }

    public SendEmailResponse sendStatusEmail(String emailAddress, StatusReport statusReport) throws NotificationClientException {

        Map<String, String> statistics = new HashMap<>();
        statistics.put("temporary_failures", Integer.toString(statusReport.getTemporaryFailedNotifications()));
        statistics.put("technical_failures", Integer.toString(statusReport.getTechnicalFailedNotifications()));
        statistics.put("permanent_failures", Integer.toString(statusReport.getPermanentlyFailedNotifications()));
        statistics.put("date", statusReport.getDate().minusDays(1).toLocalDate().toString());

        return notificationClient.sendEmail(this.statusReportEmailTemplateId, emailAddress, statistics, "");
    }
}
