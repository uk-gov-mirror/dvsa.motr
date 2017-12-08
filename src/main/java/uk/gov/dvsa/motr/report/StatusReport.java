package uk.gov.dvsa.motr.report;

import org.joda.time.DateTime;

import uk.gov.dvsa.motr.service.NotificationStatusService;
import uk.gov.service.notify.Notification;

import java.util.HashMap;
import java.util.List;

public class StatusReport {

    private int temporaryFailedNotifications = 0;
    private int permanentlyFailedNotifications = 0;
    private int technicalFailedNotifications = 0;

    private int smsTemporaryFailedNotifications = 0;
    private int smsPermanentlyFailedNotifications = 0;
    private int smsTechnicalFailedNotifications = 0;

    private DateTime reportDate;

    public StatusReport(HashMap<String, List<uk.gov.service.notify.Notification>> notifications, DateTime invokedDate) {

        temporaryFailedNotifications = getEmailCount(notifications.get(NotificationStatusService.TEMPORARY_FAILURE));
        permanentlyFailedNotifications = getEmailCount(notifications.get(NotificationStatusService.PERMANENT_FAILURE));
        technicalFailedNotifications = getEmailCount(notifications.get(NotificationStatusService.TECHNICAL_FAILURE));

        smsTemporaryFailedNotifications = getSmsCount(notifications.get(NotificationStatusService.TEMPORARY_FAILURE));
        smsPermanentlyFailedNotifications = getSmsCount(notifications.get(NotificationStatusService.PERMANENT_FAILURE));
        smsTechnicalFailedNotifications = getSmsCount(notifications.get(NotificationStatusService.TECHNICAL_FAILURE));

        reportDate = invokedDate;
    }

    public DateTime getDate() {
        return reportDate;
    }

    public int getTemporaryFailedNotifications() {

        return temporaryFailedNotifications;
    }

    public int getPermanentlyFailedNotifications() {

        return permanentlyFailedNotifications;
    }

    public int getTechnicalFailedNotifications() {

        return technicalFailedNotifications;
    }

    public int getSmsTemporaryFailedNotifications() {

        return smsTemporaryFailedNotifications;
    }

    public int getSmsPermanentlyFailedNotifications() {

        return smsPermanentlyFailedNotifications;
    }

    public int getSmsTechnicalFailedNotifications() {

        return smsTechnicalFailedNotifications;
    }

    private int getEmailCount(List<Notification> notifications) {

        int count = 0;
        for (Notification notification : notifications) {
            if (notification.getNotificationType().equals(NotificationStatusService.NOTIFICATION_TYPE_EMAIL)) {
                count ++;
            }
        }
        return count;
    }

    private int getSmsCount(List<Notification> notifications) {

        int count = 0;
        for (Notification notification : notifications) {
            if (notification.getNotificationType().equals(NotificationStatusService.NOTIFICATION_TYPE_SMS)) {
                count ++;
            }
        }
        return count;
    }

}
