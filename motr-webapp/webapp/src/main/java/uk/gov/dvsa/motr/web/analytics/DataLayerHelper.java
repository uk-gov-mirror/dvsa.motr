package uk.gov.dvsa.motr.web.analytics;

import org.json.JSONObject;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataLayerHelper {

    public static final String MESSAGE_ID = "message-id";
    public static final String MESSAGE_TEXT = "message-text";
    public static final String MESSAGE_TYPE = "message-type";
    public static final String VRM_KEY = "vrm";
    public static final String UNSUBSCRIBE_FAILURE_KEY = "unsubscribe-failure";
    public static final String DLVA_ID_KEY = "dvla-id";
    public static final String MOT_TEST_NUMBER_KEY = "mot-test-number";
    public static final String CONTACT_TYPE = "contact-type";
    public static final String VEHICLE_DATA_ORIGIN_KEY = "vehicle-data-origin";

    private Map<String, String> attributes;

    public DataLayerHelper() {
        attributes = new HashMap<>();
    }

    public void putAttribute(String key, String value) {

        attributes.put(key, value);
    }

    public Map<String, String> formatAttributes() {

        if (attributes.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> map = new HashMap<>();
        map.put("dataLayer", JSONObject.valueToString(attributes));
        return map;
    }

    public void clear() {

        attributes.clear();
    }

    public void setMessage(DataLayerMessageId messageId, DataLayerMessageType messageType, String messageText) {

        attributes.put(MESSAGE_ID, messageId.toString());
        attributes.put(MESSAGE_TYPE, messageType.toString());
        attributes.put(MESSAGE_TEXT, messageText);
    }

    public void setVehicleDataOrigin(VehicleDetails vehicleDetails) {
        if (vehicleDetails != null && vehicleDetails.getVehicleType() != null) {
            attributes.put(VEHICLE_DATA_ORIGIN_KEY, vehicleDetails.getVehicleType().toString());
        }
    }

    public void clearVehicleDataOrigin() {
        if (attributes.containsKey(VEHICLE_DATA_ORIGIN_KEY)) {
            attributes.remove(VEHICLE_DATA_ORIGIN_KEY);
        }
    }
}
