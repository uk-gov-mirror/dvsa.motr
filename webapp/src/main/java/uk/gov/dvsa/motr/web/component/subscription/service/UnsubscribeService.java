package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidUnsubscribeIdException;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.eventlog.unsubscribe.UnsubscribedEvent;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

public class UnsubscribeService {

    private SubscriptionRepository subscriptionRepository;

    @Inject
    public UnsubscribeService(SubscriptionRepository subscriptionRepository) {

        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription unsubscribe(String unsubscribeId) throws InvalidUnsubscribeIdException {

        return findSubscriptionForUnsubscribe(unsubscribeId).map(sub -> {

            subscriptionRepository.delete(sub);

            EventLogger.logEvent(new UnsubscribedEvent()
                    .setVrm(sub.getVrm())
                    .setEmail(sub.getEmail())
                    .setDueDate(sub.getMotDueDate())
            );

            return sub;

        }).orElseThrow(NotFoundException::new);
    }

    public Optional<Subscription> findSubscriptionForUnsubscribe(String unsubscribeId) {

        return subscriptionRepository.findByUnsubscribeId(unsubscribeId);
    }
}
