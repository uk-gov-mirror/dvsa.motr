package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class UpdateVrmFailedEvent extends Event {

    @Override
    public String getCode() {

        return "UPDATING-VRM-ERROR";
    }

    public UpdateVrmFailedEvent setExistingVrm(String existingVrm) {

        params.put("existing-vrm", existingVrm);
        return this;
    }

    public UpdateVrmFailedEvent setNewVrm(String newVrm) {

        params.put("new-vrm", newVrm);
        return this;
    }
}
