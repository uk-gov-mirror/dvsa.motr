package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class DvlaIdUpdatedToMotTestNumberEvent extends Event {

    @Override
    public String getCode() {

        return "DVLA-ID-UPDATED-TO-MOT-TEST-NUMBER";
    }

    public DvlaIdUpdatedToMotTestNumberEvent setExistingDvlaId(String dvlaId) {

        params.put("existing-dvla-id", dvlaId);
        return this;
    }

    public DvlaIdUpdatedToMotTestNumberEvent setNewMotTestNumber(String motTestNumber) {

        params.put("new-mot-test-number", motTestNumber);
        return this;
    }
}
