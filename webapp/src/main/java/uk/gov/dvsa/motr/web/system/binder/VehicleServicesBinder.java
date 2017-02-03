package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.web.remote.client.VehicleDetailsResponseHandler;
import uk.gov.dvsa.motr.web.service.VehicleService;
import uk.gov.dvsa.motr.web.validator.VrmValidator;

public class VehicleServicesBinder extends AbstractBinder {
    
    @Override
    protected void configure() {

        bind(VehicleService.class).to(VehicleService.class);
        bind(VrmValidator.class).to(VrmValidator.class);
        bind(VehicleDetailsResponseHandler.class).to(VehicleDetailsResponseHandler.class);
    }
}
