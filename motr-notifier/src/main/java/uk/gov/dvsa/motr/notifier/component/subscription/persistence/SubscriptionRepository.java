package uk.gov.dvsa.motr.notifier.component.subscription.persistence;

import java.time.LocalDate;

public interface SubscriptionRepository {

    void updateExpiryDate(String vrm, String email, LocalDate updatedExpiryDate);

    void updateMotTestNumber(String vrm, String email, String updatedMotTestNumber);

    void updateVrm(String vrm, String email, String updatedVrm) throws Exception;
}
