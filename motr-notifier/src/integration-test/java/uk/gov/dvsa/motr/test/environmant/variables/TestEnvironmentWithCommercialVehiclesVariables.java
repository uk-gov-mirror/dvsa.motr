package uk.gov.dvsa.motr.test.environmant.variables;

import static uk.gov.dvsa.motr.notifier.SystemVariable.FEATURE_TOGGLE_HGV_PSV_VEHICLES;


public class TestEnvironmentWithCommercialVehiclesVariables extends TestEnvironmentVariables {

    public TestEnvironmentWithCommercialVehiclesVariables() {
        super();
        set(FEATURE_TOGGLE_HGV_PSV_VEHICLES.getName(), "true");
    }
}
