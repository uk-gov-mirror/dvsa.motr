package uk.gov.dvsa.motr.service;

import uk.gov.dvsa.motr.event.AllBouncingEmailsProcessedEvent;
import uk.gov.dvsa.motr.event.CleanUpTriggeredEvent;
import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.persistence.entity.SubscriptionDbItem;
import uk.gov.dvsa.motr.persistence.repository.CancelledSubscriptionRepository;
import uk.gov.dvsa.motr.persistence.repository.SubscriptionRepository;
import uk.gov.dvsa.motr.report.BouncingEmailCleanerReport;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;

public class UnsubscribeBouncingEmailAddressService {

    private SubscriptionRepository subscriptionRepository;
    private CancelledSubscriptionRepository cancelledSubscriptionRepository;
    private EmailMessageStatusService emailMessageStatusService;

    public UnsubscribeBouncingEmailAddressService(
            SubscriptionRepository subscriptionRepository,
            CancelledSubscriptionRepository cancelledSubscriptionRepository,
            EmailMessageStatusService emailMessageStatusService) {

        this.subscriptionRepository = subscriptionRepository;
        this.cancelledSubscriptionRepository = cancelledSubscriptionRepository;
        this.emailMessageStatusService = emailMessageStatusService;
    }

    public BouncingEmailCleanerReport run() throws NotificationClientException {

        EventLogger.logEvent(new CleanUpTriggeredEvent());

        BouncingEmailCleanerReport report = new BouncingEmailCleanerReport();
        report.startCleanUp();

        List<String> emailAddressesForPermanentlyFailingEmails
                = emailMessageStatusService.getEmailAddressesAssociatedWithPermanentlyFailingNotifications();

        List<SubscriptionDbItem> recordsForPermanentlyBouncingEmailAddresses
                = subscriptionRepository.findByEmails(emailAddressesForPermanentlyFailingEmails);

        for (SubscriptionDbItem subscriptionDbItem : recordsForPermanentlyBouncingEmailAddresses) {
            subscriptionRepository.deleteRecord(subscriptionDbItem);
            cancelledSubscriptionRepository.cancelSubscription(subscriptionDbItem);

            report.incrementSuccessfullyCancelled();
        }

        EventLogger.logEvent(new AllBouncingEmailsProcessedEvent()
                .setDurationOfCleanUp(report.getDurationOfCleanUp())
                .setNumberOfSubscriptionsCancelled(report.getNumberOfSubscriptionsSuccessfullyCancelled())
        );

        return report;
    }
}
