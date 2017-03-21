package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.helper.EmailConfirmationUrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.eventlog.subscription.PendingSubscriptionCreatedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.PendingSubscriptionCreationFailedEvent;

import java.time.LocalDate;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.component.subscription.service.RandomIdGenerator.generateId;

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

    /**
     * Creates pending subscription in the system to be confirmed later by confirmation link
     * @param vrm subscription vrm
     * @param email subscription email
     * @param motDueDate most recent mot due date
     * @throws SubscriptionAlreadyExistsException thrown when subscription against specific email and vrm exists
     */
    public void createPendingSubscription(String vrm, String email, LocalDate motDueDate)
            throws SubscriptionAlreadyExistsException {

        if (subscriptionRepository.findByVrmAndEmail(vrm, email).isPresent()) {
            throw new SubscriptionAlreadyExistsException("Subscription for vrm: {} and email: {} exists!");
        }

        PendingSubscription pendingSubscription = new PendingSubscription()
                .setConfirmationId(generateId())
                .setEmail(email)
                .setVrm(vrm)
                .setMotDueDate(motDueDate);

        try {
            pendingSubscriptionRepository.save(pendingSubscription);

            notifyService.sendEmailAddressConfirmationEmail(email, emailConfirmationUrlHelper.build(pendingSubscription));
            EventLogger.logEvent(new PendingSubscriptionCreatedEvent().setVrm(vrm).setEmail(email).setMotDueDate(motDueDate));

        } catch (Exception e) {

            EventLogger.logErrorEvent(
                    new PendingSubscriptionCreationFailedEvent().setVrm(vrm).setEmail(email).setMotDueDate(motDueDate), e
            );
            throw e;
        }
    }
}
