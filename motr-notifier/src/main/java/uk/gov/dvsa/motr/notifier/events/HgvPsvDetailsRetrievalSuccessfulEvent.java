package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class HgvPsvDetailsRetrievalSuccessfulEvent extends Event {

    @Override
    public String getCode() {

        return "HGV-PSV-DETAILS-RETRIEVAL-SUCCESSFUL";
    }
}
