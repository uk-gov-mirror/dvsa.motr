package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyConfirmedException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.eventlog.subscription.InvalidSubscriptionConfirmationIdUsedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SubscriptionConfirmationFailedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SubscriptionConfirmedEvent;

import java.util.Optional;

import javax.inject.Inject;

public class SubscriptionConfirmationService {

    private final NotifyService notifyService;
    private final UrlHelper urlHelper;
    private final PendingSubscriptionRepository pendingSubscriptionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final VehicleDetailsClient client;

    @Inject
    public SubscriptionConfirmationService(
            PendingSubscriptionRepository pendingSubscriptionRepository,
            SubscriptionRepository subscriptionRepository,
            NotifyService notifyService,
            UrlHelper urlHelper,
            VehicleDetailsClient client
    ) {

        this.pendingSubscriptionRepository = pendingSubscriptionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notifyService = notifyService;
        this.urlHelper = urlHelper;
        this.client = client;
    }

    public Subscription confirmSubscription(String confirmationId) throws SubscriptionAlreadyConfirmedException,
            InvalidConfirmationIdException {

        Optional<PendingSubscription> pendingSubscription = pendingSubscriptionRepository.findByConfirmationId(confirmationId);
        if (pendingSubscription.isPresent()) {
            return confirm(pendingSubscription.get());
        }

        Optional<Subscription> existingSubscription = subscriptionRepository.findByUnsubscribeId(confirmationId);
        if (existingSubscription.isPresent()) {
            throw new SubscriptionAlreadyConfirmedException(existingSubscription.get());
        }

        EventLogger.logEvent(new InvalidSubscriptionConfirmationIdUsedEvent().setUsedId(confirmationId));
        throw new InvalidConfirmationIdException();
    }
    
    private Subscription confirm(PendingSubscription pendingSubscription) {

        try {
            Subscription subscription = applyPendingSubscription(pendingSubscription);

            notifyService.sendSubscriptionConfirmation(subscription);

            EventLogger.logEvent(new SubscriptionConfirmedEvent()
                    .setVrm(subscription.getVrm())
                    .setEmail(subscription.getContactDetail().getValue())
                    .setDueDate(subscription.getMotDueDate()));

            return subscription;

        } catch (Exception e) {

            EventLogger.logErrorEvent(new SubscriptionConfirmationFailedEvent()
                    .setVrm(pendingSubscription.getVrm())
                    .setEmail(pendingSubscription.getContactDetail().getValue())
                    .setDueDate(pendingSubscription.getMotDueDate()));
            throw e;
        }
    }

    private Subscription applyPendingSubscription(PendingSubscription pendingSubscription) {

        Subscription subscription = new Subscription()
                .setUnsubscribeId(pendingSubscription.getConfirmationId())
                .setVrm(pendingSubscription.getVrm())
                .setContactDetail(pendingSubscription.getContactDetail())
                .setMotDueDate(pendingSubscription.getMotDueDate())
                .setVin(pendingSubscription.getVin().orElse(null))
                .setVehicleType(pendingSubscription.getVehicleType())
                .setMotIdentification(pendingSubscription.getMotIdentification());

        subscriptionRepository.save(subscription);
        pendingSubscriptionRepository.delete(pendingSubscription);

        return subscription;
    }
}
