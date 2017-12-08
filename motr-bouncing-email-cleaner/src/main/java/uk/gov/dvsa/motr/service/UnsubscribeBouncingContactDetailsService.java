package uk.gov.dvsa.motr.service;

import org.joda.time.DateTime;

import uk.gov.dvsa.motr.event.AllBouncingEmailsProcessedEvent;
import uk.gov.dvsa.motr.event.CleanUpTriggeredEvent;
import uk.gov.dvsa.motr.event.StatusReportEvent;
import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.persistence.entity.SubscriptionDbItem;
import uk.gov.dvsa.motr.persistence.repository.CancelledSubscriptionRepository;
import uk.gov.dvsa.motr.persistence.repository.SubscriptionRepository;
import uk.gov.dvsa.motr.report.BouncingEmailCleanerReport;
import uk.gov.dvsa.motr.report.StatusReport;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnsubscribeBouncingContactDetailsService {

    private SubscriptionRepository subscriptionRepository;
    private CancelledSubscriptionRepository cancelledSubscriptionRepository;
    private NotificationStatusService notificationStatusService;
    private SendStatusReportService sendStatusReportService;

    public UnsubscribeBouncingContactDetailsService(
            SubscriptionRepository subscriptionRepository,
            CancelledSubscriptionRepository cancelledSubscriptionRepository,
            NotificationStatusService notificationStatusService, SendStatusReportService sendStatusReportService) {

        this.subscriptionRepository = subscriptionRepository;
        this.cancelledSubscriptionRepository = cancelledSubscriptionRepository;
        this.notificationStatusService = notificationStatusService;
        this.sendStatusReportService = sendStatusReportService;
    }

    public BouncingEmailCleanerReport run(DateTime invokedDate) throws NotificationClientException {

        EventLogger.logEvent(new CleanUpTriggeredEvent());

        BouncingEmailCleanerReport report = new BouncingEmailCleanerReport();
        report.startCleanUp();

        HashMap<String, List<Notification>> notifications = getNotifications(invokedDate);

        StatusReport statusReport = new StatusReport(notifications, invokedDate);

        // Note: contactDetails could email or phone number.
        List<String> contactDetails = getContactDetails(notifications, NotificationStatusService.PERMANENT_FAILURE);

        List<SubscriptionDbItem> recordsForPermanentlyBouncingEmailAddresses = subscriptionRepository.findByEmails(contactDetails);

        for (SubscriptionDbItem subscriptionDbItem : recordsForPermanentlyBouncingEmailAddresses) {

            subscriptionRepository.deleteRecord(subscriptionDbItem);
            cancelledSubscriptionRepository.cancelSubscription(subscriptionDbItem);

            report.incrementSuccessfullyCancelled();
        }

        EventLogger.logEvent(new AllBouncingEmailsProcessedEvent()
                .setDurationOfCleanUp(report.getDurationOfCleanUp())
                .setNumberOfSubscriptionsCancelled(report.getNumberOfSubscriptionsSuccessfullyCancelled())
        );

        EventLogger.logEvent(new StatusReportEvent()
                .setNumberOfPermanentlyFailedNotifications(statusReport.getPermanentlyFailedNotifications())
                .setNumberOfTechnicalFailedNotifications(statusReport.getTechnicalFailedNotifications())
                .setNumberOfTemporaryFailedNotifications(statusReport.getTemporaryFailedNotifications())
                .setNumberOfSmsPermanentlyFailedNotifications(statusReport.getSmsPermanentlyFailedNotifications())
                .setNumberOfSmsTechnicalFailedNotifications(statusReport.getSmsTechnicalFailedNotifications())
                .setNumberOfSmsTemporaryFailedNotifications(statusReport.getSmsTemporaryFailedNotifications())
        );

        sendStatusReportService.sendNotifications(statusReport);

        return report;
    }

    private HashMap<String, List<Notification>> getNotifications(DateTime invokedDate) throws NotificationClientException {

        HashMap<String, List<Notification>> notifications = new HashMap<>();
        putNotifications(notifications, NotificationStatusService.PERMANENT_FAILURE, invokedDate);
        putNotifications(notifications, NotificationStatusService.TEMPORARY_FAILURE, invokedDate);
        putNotifications(notifications, NotificationStatusService.TECHNICAL_FAILURE, invokedDate);
        return notifications;
    }

    private void putNotifications(HashMap<String, List<Notification>> notifications, String status, DateTime invokedDate)
        throws NotificationClientException {

        notifications.put(status, notificationStatusService.getFilteredNotifications(status, invokedDate));
    }

    private List<String> getContactDetails(HashMap<String, List<Notification>> notifications, String status) {

        List<String> contactDetails = new ArrayList();

        for (Notification notification : notifications.get(status)) {

            if (notification.getNotificationType().equals(NotificationStatusService.NOTIFICATION_TYPE_EMAIL)) {
                contactDetails.add(notification.getEmailAddress().get());
            } else if (notification.getNotificationType().equals(NotificationStatusService.NOTIFICATION_TYPE_SMS)) {
                contactDetails.add(notification.getPhoneNumber().get());
            }
        }
        return contactDetails;
    }


}
