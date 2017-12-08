package uk.gov.dvsa.motr.report;

import org.joda.time.DateTime;

import uk.gov.dvsa.motr.service.EmailMessageStatusService;

import java.util.HashMap;
import java.util.List;

public class StatusReport {

    private int temporaryFailedNotifications = 0;
    private int permanentlyFailedNotifications = 0;
    private int technicalFailedNotifications = 0;
    private DateTime reportDate;

    public StatusReport(HashMap<String, List<String>> emailAddresses, DateTime invokedDate) {

        setTechnicalFailedNotifications(emailAddresses.get(EmailMessageStatusService.TECHNICAL_FAILURE_MESSAGE_STATUS).size());
        setPermanentlyFailedNotifications(emailAddresses.get(EmailMessageStatusService.PERMANENT_FAILURE_MESSAGE_STATUS).size());
        setTemporaryFailedNotifications(emailAddresses.get(EmailMessageStatusService.TEMPORARY_FAILURE_MESSAGE_STATUS).size());
        setDate(invokedDate);
    }

    public DateTime getDate() {
        return reportDate;
    }

    private void setDate(DateTime reportDate) {
        this.reportDate = reportDate;
    }

    public int getTemporaryFailedNotifications() {
        return temporaryFailedNotifications;
    }

    private void setTemporaryFailedNotifications(int temporaryFailedNotifications) {
        this.temporaryFailedNotifications = temporaryFailedNotifications;
    }

    public int getPermanentlyFailedNotifications() {
        return permanentlyFailedNotifications;
    }

    private void setPermanentlyFailedNotifications(int permanentlyFailedNotifications) {
        this.permanentlyFailedNotifications = permanentlyFailedNotifications;
    }

    public int getTechnicalFailedNotifications() {
        return technicalFailedNotifications;
    }

    private void setTechnicalFailedNotifications(int technicalFailedNotifications) {
        this.technicalFailedNotifications = technicalFailedNotifications;
    }
}
