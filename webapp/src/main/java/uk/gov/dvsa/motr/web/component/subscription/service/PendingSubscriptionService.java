package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.eventlog.subscription.PendingSubscriptionCreatedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.PendingSubscriptionCreationFailedEvent;
import uk.gov.dvsa.motr.web.helper.EmailConfirmationUrlHelper;

import java.time.LocalDate;

import javax.inject.Inject;

public class PendingSubscriptionService {

    private PendingSubscriptionRepository pendingSubscriptionRepository;
    private SubscriptionRepository subscriptionRepository;
    private NotifyService notifyService;
    private EmailConfirmationUrlHelper emailConfirmationUrlHelper;

    @Inject
    public PendingSubscriptionService(
            PendingSubscriptionRepository pendingSubscriptionRepository,
            SubscriptionRepository subscriptionRepository,
            NotifyService notifyService,
            EmailConfirmationUrlHelper emailConfirmationUrlHelper
    ) {

        this.pendingSubscriptionRepository = pendingSubscriptionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notifyService = notifyService;
        this.emailConfirmationUrlHelper = emailConfirmationUrlHelper;
    }

    public void createPendingSubscription(String vrm, String email, LocalDate motExpiryDate)
            throws SubscriptionAlreadyExistsException {

        if (subscriptionRepository.findByVrmAndEmail(vrm, email).isPresent()) {
            throw new SubscriptionAlreadyExistsException("Subscription for vrm: {} and email: {} exists!");
        }

        PendingSubscription pendingSubscription = new PendingSubscription(RandomIdGenerator.getRandomId())
                .setEmail(email)
                .setVrm(vrm)
                .setMotDueDate(motExpiryDate);

        try {
            pendingSubscriptionRepository.save(pendingSubscription);

            notifyService.sendEmailAddressConfirmationEmail(email, emailConfirmationUrlHelper.build(pendingSubscription));
            EventLogger.logEvent(new PendingSubscriptionCreatedEvent().setVrm(vrm).setEmail(email).setExpiryDate(motExpiryDate));

        } catch (Exception e) {

            EventLogger.logErrorEvent(
                    new PendingSubscriptionCreationFailedEvent().setVrm(vrm).setEmail(email).setExpiryDate(motExpiryDate), e
            );
            throw e;
        }
    }
}
