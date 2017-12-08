package uk.gov.dvsa.motr.web.eventlog.vehicle;

import uk.gov.dvsa.motr.eventlog.Event;

public class VehicleDetailsExceptionEvent extends Event {

    @Override
    public String getCode() {

        return "TRADE-API-ERROR";
    }

    public VehicleDetailsExceptionEvent setVrm(String vrm) {

        params.put("searched-for-vrm", vrm);
        return this;
    }
}
