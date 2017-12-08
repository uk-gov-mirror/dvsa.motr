package uk.gov.dvsa.motr.smsreceiver.subscription.persistence;


import com.amazonaws.services.dynamodbv2.document.Item;

import uk.gov.dvsa.motr.smsreceiver.subscription.model.CancelledSubscription;

import java.util.Iterator;

public interface CancelledSubscriptionRepository {

    void save(CancelledSubscription cancelledSubscription);

    Iterator<Item> findCancelledSubscriptionByVrmAndMobile(String vrm, String mobileNumber);
}
