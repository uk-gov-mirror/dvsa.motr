package uk.gov.dvsa.motr.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngine;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineException;
import uk.gov.dvsa.motr.notify.NotifyTemplateEngineFailedEvent;
import uk.gov.dvsa.motr.report.StatusReport;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.util.HashMap;
import java.util.Map;

public class NotifyService {

    private NotificationClient notificationClient;
    private String statusReportEmailTemplateId;
    private NotifyTemplateEngine notifyTemplateEngine;
    private static final String MOTR_BOUNCING_EMAIL_CLEANER_FAILED_STATUS_REPORT_SUBJECT =
            "motr-bouncing-email-cleaner-failed-status-report-subject.txt";
    private static final String MOTR_BOUNCING_EMAIL_CLEANER_FAILED_STATUS_REPORT_BODY =
            "motr-bouncing-email-cleaner-failed-status-report-body.txt";

    public NotifyService(
            NotificationClient notificationClient,
            String statusReportEmailTemplateId,
            NotifyTemplateEngine notifyTemplateEngine
    ) {

        this.notificationClient = notificationClient;
        this.statusReportEmailTemplateId = statusReportEmailTemplateId;
        this.notifyTemplateEngine = notifyTemplateEngine;
    }

    public SendEmailResponse sendStatusEmail(String emailAddress, StatusReport statusReport)
            throws NotificationClientException {

        Map<String, String> statistics = new HashMap<>();
        statistics.put("temporary_failures",
                Integer.toString(statusReport.getTemporaryFailedNotifications()) + "(email) " +
                Integer.toString(statusReport.getSmsTemporaryFailedNotifications()) + "(SMS) "
        );
        statistics.put("technical_failures",
                Integer.toString(statusReport.getTechnicalFailedNotifications()) + "(email) " +
                Integer.toString(statusReport.getSmsTechnicalFailedNotifications()) + "(SMS) "
        );
        statistics.put("permanent_failures",
                Integer.toString(statusReport.getPermanentlyFailedNotifications()) + "(email) " +
                Integer.toString(statusReport.getSmsPermanentlyFailedNotifications()) + "(SMS) "
        );
        statistics.put("date", statusReport.getDate().minusDays(1).toLocalDate().toString());

        try {
            statistics = notifyTemplateEngine.getNotifyParameters(
                    MOTR_BOUNCING_EMAIL_CLEANER_FAILED_STATUS_REPORT_SUBJECT,
                    MOTR_BOUNCING_EMAIL_CLEANER_FAILED_STATUS_REPORT_BODY,
                    statistics);
        } catch (NotifyTemplateEngineException exception) {
            EventLogger.logErrorEvent(
                    new NotifyTemplateEngineFailedEvent().setType(NotifyTemplateEngineFailedEvent.Type.ERROR_GETTING_PARAMETERS),
                    exception);
            // wrapping because nothing can be done about it
            throw new RuntimeException(exception);
        }
        return notificationClient.sendEmail(this.statusReportEmailTemplateId, emailAddress, statistics, "");
    }
}
