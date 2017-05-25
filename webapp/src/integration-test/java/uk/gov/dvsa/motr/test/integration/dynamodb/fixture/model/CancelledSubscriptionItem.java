package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model;

import uk.gov.dvsa.motr.test.data.RandomDataUtil;

import java.util.UUID;

public class CancelledSubscriptionItem {

    private String unsubscribeId = UUID.randomUUID().toString();

    private String vrm = RandomDataUtil.vrm();

    private String email = RandomDataUtil.email();

    public String getUnsubscribeId() {
        return unsubscribeId;
    }

    public String getVrm() {
        return vrm;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "CancelledSubscriptionItem{" +
                "unsubscribeId='" + unsubscribeId + '\'' +
                ", vrm='" + vrm + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
