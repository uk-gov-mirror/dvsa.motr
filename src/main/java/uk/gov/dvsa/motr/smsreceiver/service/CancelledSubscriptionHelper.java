package uk.gov.dvsa.motr.smsreceiver.service;

import com.amazonaws.services.dynamodbv2.document.Item;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.smsreceiver.events.SuccessfullyUnsubscribedEvent;
import uk.gov.dvsa.motr.smsreceiver.subscription.model.CancelledSubscription;
import uk.gov.dvsa.motr.smsreceiver.subscription.model.Subscription;
import uk.gov.dvsa.motr.smsreceiver.subscription.persistence.CancelledSubscriptionRepository;

import java.util.Iterator;

public class CancelledSubscriptionHelper {

    private static final String REASON_FOR_CANCELLATION_USER_CANCELLED = "User cancelled";

    private CancelledSubscriptionRepository cancelledSubscriptionRepository;

    public CancelledSubscriptionHelper(CancelledSubscriptionRepository cancelledSubscriptionRepository) {

        this.cancelledSubscriptionRepository = cancelledSubscriptionRepository;
    }

    public void createANewCancelledSubscriptionEntry(Subscription subscription) {

        CancelledSubscription cancelledSubscription = mapToCancelledSubscription(subscription);
        cancelledSubscriptionRepository.save(cancelledSubscription);

        EventLogger.logEvent(new SuccessfullyUnsubscribedEvent()
                .setVrm(subscription.getVrm())
                .setDueDate(subscription.getMotDueDate())
                .setReasonForCancellation(REASON_FOR_CANCELLATION_USER_CANCELLED));
    }

    public boolean foundMatchingCancelledSubscription(String vrm, String mobileNumber) {

        Iterator<Item> items = cancelledSubscriptionRepository.findCancelledSubscriptionByVrmAndMobile(vrm, mobileNumber);
        while (items.hasNext()) {
            Item item = items.next();
            if (vrm.equalsIgnoreCase(item.getString("vrm")) && mobileNumber.equals(item.getString("email"))) {
                return true;
            }
        }
        return false;
    }

    private CancelledSubscription mapToCancelledSubscription(Subscription subscription) {

        return new CancelledSubscription()
                .setUnsubscribeId(subscription.getUnsubscribeId())
                .setVrm(subscription.getVrm())
                .setEmail(subscription.getContactDetail())
                .setReasonForCancellation(REASON_FOR_CANCELLATION_USER_CANCELLED);
    }
}
