package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class UpdateVrmSuccessfulEvent extends Event {

    @Override
    public String getCode() {

        return "SUCCESSFULLY-UPDATED-VRM";
    }

    public UpdateVrmSuccessfulEvent setExistingVrm(String existingVrm) {

        params.put("existing-vrm", existingVrm);
        return this;
    }

    public UpdateVrmSuccessfulEvent setNewVrm(String newVrm) {

        params.put("new-vrm", newVrm);
        return this;
    }
}
