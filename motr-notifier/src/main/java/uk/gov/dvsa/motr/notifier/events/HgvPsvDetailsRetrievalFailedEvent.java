package uk.gov.dvsa.motr.notifier.events;

public class HgvPsvDetailsRetrievalFailedEvent extends SubscriptionProcessedEvent {

    @Override
    public String getCode() {

        return "HGV-PSV-DETAILS-RETRIEVAL-FAILED";
    }
}
