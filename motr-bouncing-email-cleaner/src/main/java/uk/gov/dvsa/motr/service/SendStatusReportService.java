package uk.gov.dvsa.motr.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.report.StatusReport;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;

public class SendStatusReportService {

    private NotifyService notifyService;
    private List<String> recipients;

    private static final Logger logger = LoggerFactory.getLogger(EventLogger.class);

    public SendStatusReportService(NotifyService notifyService, List<String> recipients) {

        this.notifyService = notifyService;
        this.recipients = recipients;
    }

    public void sendNotifications(StatusReport statusReport) throws NotificationClientException {

        for (String recipient : this.recipients) {
            logger.info("Sending status email to: {}", recipient);
            this.notifyService.sendStatusEmail(recipient, statusReport);
        }
    }
}
