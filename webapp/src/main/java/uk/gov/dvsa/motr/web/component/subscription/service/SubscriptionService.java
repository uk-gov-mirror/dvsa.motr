package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.UUID;

import javax.inject.Inject;

import static java.lang.String.format;

public class SubscriptionService {

    private SubscriptionRepository subscriptionRepository;
    private final NotifyService notifyService;

    @Inject
    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            NotifyService notifyService
    ) {

        this.subscriptionRepository = subscriptionRepository;
        this.notifyService = notifyService;
    }

    public void createSubscription(String vrm, String email, LocalDate motDueDate) throws SubscriptionAlreadyExistsException,
            NotificationClientException {

        if (!doesSubscriptionAlreadyExist(vrm, email)) {
            Subscription subscription = new Subscription(UUID.randomUUID().toString())
                    .setEmail(email)
                    .setVrm(vrm)
                    .setMotDueDate(motDueDate);
            this.subscriptionRepository.save(subscription);
            this.notifyService.sendConfirmationEmail(email, vrm, motDueDate, "link");
        } else {
            throw new SubscriptionAlreadyExistsException(format("A subscription exists for vehicle: %s with an email of: %s", vrm, email));
        }
    }

    private boolean doesSubscriptionAlreadyExist(String vrm, String email) {

        return this.subscriptionRepository.findByVrmAndEmail(vrm, email).isPresent();
    }
}
