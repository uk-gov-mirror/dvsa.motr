package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidActivationIdException;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.eventlog.subscription.InvalidSubscriptionActivationIdUsedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SubscriptionActivatedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SubscriptionActivationFailedEvent;
import uk.gov.dvsa.motr.web.helper.UnsubscriptionUrlHelper;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

public class PendingSubscriptionActivatorService {

    private final NotifyService notifyService;
    private final UnsubscriptionUrlHelper unsubscriptionUrlHelper;
    private final PendingSubscriptionRepository pendingSubscriptionRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Inject
    public PendingSubscriptionActivatorService(
            PendingSubscriptionRepository pendingSubscriptionRepository,
            SubscriptionRepository subscriptionRepository,
            NotifyService notifyService,
            UnsubscriptionUrlHelper unsubscriptionUrlHelper
    ) {

        this.pendingSubscriptionRepository = pendingSubscriptionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notifyService = notifyService;
        this.unsubscriptionUrlHelper = unsubscriptionUrlHelper;
    }

    public Subscription activateSubscription(String subscriptionId) throws InvalidActivationIdException {

        Optional<PendingSubscription> pendingSubscriptionOptional = pendingSubscriptionRepository.findById(subscriptionId);

        if (pendingSubscriptionOptional.isPresent()) {
            PendingSubscription pendingSubscription = pendingSubscriptionOptional.get();

            try {
                Subscription subscription = applyPendingSubscription(pendingSubscription);
                sendSubscriptionConfirmationEmail(subscription);

                EventLogger.logEvent(new SubscriptionActivatedEvent()
                        .setVrm(subscription.getVrm())
                        .setEmail(subscription.getEmail())
                        .setExpiryDate(subscription.getMotDueDate()));

                return subscription;

            } catch (Exception e) {

                EventLogger.logErrorEvent(new SubscriptionActivationFailedEvent()
                        .setVrm(pendingSubscription.getVrm())
                        .setEmail(pendingSubscription.getEmail())
                        .setExpiryDate(pendingSubscription.getMotDueDate()));
                throw e;
            }

        } else {
            EventLogger.logEvent(new InvalidSubscriptionActivationIdUsedEvent());
            throw new InvalidActivationIdException();
        }
    }

    private Subscription applyPendingSubscription(PendingSubscription pendingSubscription) {

        Subscription subscription = new Subscription(UUID.randomUUID().toString())
                .setVrm(pendingSubscription.getVrm())
                .setEmail(pendingSubscription.getEmail())
                .setMotDueDate(pendingSubscription.getMotDueDate());

        subscriptionRepository.save(subscription);
        pendingSubscriptionRepository.delete(pendingSubscription);

        return subscription;
    }

    private void sendSubscriptionConfirmationEmail(Subscription subscription) {

        notifyService.sendSubscriptionConfirmationEmail(
                subscription.getEmail(),
                subscription.getVrm(),
                subscription.getMotDueDate(),
                unsubscriptionUrlHelper.build(subscription.getId()));
    }
}
