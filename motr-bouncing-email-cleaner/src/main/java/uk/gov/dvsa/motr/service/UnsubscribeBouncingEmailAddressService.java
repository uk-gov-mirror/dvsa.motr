package uk.gov.dvsa.motr.service;

import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;

import org.joda.time.DateTime;

import uk.gov.dvsa.motr.event.AllBouncingEmailsProcessedEvent;
import uk.gov.dvsa.motr.event.CleanUpTriggeredEvent;
import uk.gov.dvsa.motr.event.StatusEmailsSentEvent;
import uk.gov.dvsa.motr.event.StatusReportEvent;
import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.persistence.entity.SubscriptionDbItem;
import uk.gov.dvsa.motr.persistence.repository.CancelledSubscriptionRepository;
import uk.gov.dvsa.motr.persistence.repository.SubscriptionRepository;
import uk.gov.dvsa.motr.report.BouncingEmailCleanerReport;
import uk.gov.dvsa.motr.report.StatusReport;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.List;

public class UnsubscribeBouncingEmailAddressService {

    private SubscriptionRepository subscriptionRepository;
    private CancelledSubscriptionRepository cancelledSubscriptionRepository;
    private EmailMessageStatusService emailMessageStatusService;
    private SendStatusReportService sendStatusReportService;

    public UnsubscribeBouncingEmailAddressService(
            SubscriptionRepository subscriptionRepository,
            CancelledSubscriptionRepository cancelledSubscriptionRepository,
            EmailMessageStatusService emailMessageStatusService, SendStatusReportService sendStatusReportService) {

        this.subscriptionRepository = subscriptionRepository;
        this.cancelledSubscriptionRepository = cancelledSubscriptionRepository;
        this.emailMessageStatusService = emailMessageStatusService;
        this.sendStatusReportService = sendStatusReportService;
    }

    public BouncingEmailCleanerReport run(DateTime invokedDate) throws NotificationClientException {

        EventLogger.logEvent(new CleanUpTriggeredEvent());

        BouncingEmailCleanerReport report = new BouncingEmailCleanerReport();
        report.startCleanUp();

        HashMap<String, List<String>> emailAddresses = getNotifications(invokedDate);

        StatusReport statusReport = new StatusReport(emailAddresses, invokedDate);

        List<SubscriptionDbItem> recordsForPermanentlyBouncingEmailAddresses = subscriptionRepository.findByEmails(
                emailAddresses.get(EmailMessageStatusService.PERMANENT_FAILURE_MESSAGE_STATUS));

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
        );

        sendStatusReportService.sendNotifications(statusReport);

        return report;
    }

    private HashMap<String, List<String>> getNotifications(DateTime notificationDateFilter) throws NotificationClientException {

        HashMap<String, List<String>> emailAddresses = new HashMap<>();

        putNotifications(emailAddresses, EmailMessageStatusService.PERMANENT_FAILURE_MESSAGE_STATUS, notificationDateFilter);
        putNotifications(emailAddresses, EmailMessageStatusService.TEMPORARY_FAILURE_MESSAGE_STATUS, notificationDateFilter);
        putNotifications(emailAddresses, EmailMessageStatusService.TECHNICAL_FAILURE_MESSAGE_STATUS, notificationDateFilter);

        return emailAddresses;
    }

    private void putNotifications(HashMap<String, List<String>> emailAddresses, String status, DateTime notificationDateFilter)
            throws NotificationClientException {

        emailAddresses.put(status, emailMessageStatusService.getEmailAddressesAssociatedWithNotifications(status, notificationDateFilter));
    }
}
