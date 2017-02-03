package uk.gov.dvsa.motr.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.web.remote.client.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.remote.client.VehicleDetailsResponseHandler;
import uk.gov.dvsa.motr.web.remote.client.VehicleNotFoundException;
import uk.gov.dvsa.motr.web.validator.VrmValidator;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    private static final String REGISTRATION_NOT_FOUND_MESSAGE = "Check that you’ve typed in the correct registration number.<br/>" +
            "<br/>You can only sign up if your vehicle has had its first MOT.";
    private static final String VEHICLE_DETAILS_KEY = "vehicleDetails";
    private static final String MESSAGE_KEY = "message";
    private static final String SHOW_INLINE_KEY = "showInLine";

    private final VrmValidator vrmValidator;
    private VehicleDetailsClient vehicleDetailsClient;
    private VehicleDetailsResponseHandler vehicleDetailsResponseHandler;

    @Inject
    public VehicleService(VrmValidator vrmValidator, VehicleDetailsClient vehicleDetailsClient, VehicleDetailsResponseHandler
            vehicleDetailsResponseHandler) {

        this.vrmValidator = vrmValidator;
        this.vehicleDetailsClient = vehicleDetailsClient;
        this.vehicleDetailsResponseHandler = vehicleDetailsResponseHandler;
    }

    public Map<String, Object> createVehicleResponseMap(String registrationNumber) {

        registrationNumber = registrationNumber.replaceAll("\\s+", "").toUpperCase();

        Map<String, Object> vehicleDetailsMap = new HashMap<>();
        vehicleDetailsMap.put(VEHICLE_DETAILS_KEY, registrationNumber);

        boolean vrmFormatIsValid = this.vrmValidator.isValid(registrationNumber);
        if (!vrmFormatIsValid) {
            vehicleDetailsMap.put(MESSAGE_KEY, this.vrmValidator.getMessage());
            vehicleDetailsMap.put(SHOW_INLINE_KEY, this.vrmValidator.shouldShowInLineMessage());

            return vehicleDetailsMap;
        }

        try {
            Long dateTimeBeforeClientCall = System.currentTimeMillis();
            Response response = this.vehicleDetailsClient.retrieveVehicleDetails(registrationNumber);
            Long dateTimeAfterClientCall = System.currentTimeMillis();
            logger.info("Response time from vehicle client was: {}", dateTimeAfterClientCall - dateTimeBeforeClientCall);
            this.vehicleDetailsResponseHandler.getVehicleDetailsFromResponse(response, registrationNumber);
        } catch (VehicleNotFoundException e) {
            vehicleDetailsMap.put(MESSAGE_KEY, REGISTRATION_NOT_FOUND_MESSAGE);
            vehicleDetailsMap.put(SHOW_INLINE_KEY, false);
        } catch (ServerErrorException | ClientErrorException e) {
            //TODO this is to be covered in BL-4200
            //we will show a something went wrong banner message, so we will thread that
            //through from here
        }

        return vehicleDetailsMap;
    }
}
