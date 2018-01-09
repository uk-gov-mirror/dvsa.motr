package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidUnsubscribeIdException;
import uk.gov.dvsa.motr.web.component.subscription.model.CancelledSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.CancelledSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.eventlog.unsubscribe.UnsubscribedEvent;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

public class UnsubscribeService {

    private static final String REASON_FOR_CANCELLATION_USER_CANCELLED = "User cancelled";

    private final SubscriptionRepository subscriptionRepository;
    private final CancelledSubscriptionRepository cancelledSubscriptionRepository;

    @Inject
    public UnsubscribeService(SubscriptionRepository subscriptionRepository,
            CancelledSubscriptionRepository cancelledSubscriptionRepository) {

        this.subscriptionRepository = subscriptionRepository;
        this.cancelledSubscriptionRepository = cancelledSubscriptionRepository;
    }

    public Subscription unsubscribe(String unsubscribeId) throws InvalidUnsubscribeIdException {

        return findSubscriptionForUnsubscribe(unsubscribeId).map(sub -> {

            CancelledSubscription cancelledSubscription = mapToCancelledSubscription(sub);

            cancelledSubscriptionRepository.save(cancelledSubscription);

            subscriptionRepository.delete(sub);

            EventLogger.logEvent(new UnsubscribedEvent()
                    .setVrm(sub.getVrm())
                    .setEmail(sub.getContactDetail().getValue())
                    .setDueDate(sub.getMotDueDate())
                    .setReasonForCancellation(REASON_FOR_CANCELLATION_USER_CANCELLED)
            );

            return sub;

        }).orElseThrow(NotFoundException::new);
    }

    public Optional<Subscription> findSubscriptionForUnsubscribe(String unsubscribeId) {

        return subscriptionRepository.findByUnsubscribeId(unsubscribeId);
    }

    private CancelledSubscription mapToCancelledSubscription(Subscription subscription) {

        return new CancelledSubscription()
                .setUnsubscribeId(subscription.getUnsubscribeId())
                .setVrm(subscription.getVrm())
                .setContactDetail(subscription.getContactDetail())
                .setReasonForCancellation(REASON_FOR_CANCELLATION_USER_CANCELLED);
    }
}
