package uk.gov.dvsa.motr.web.analytics;

import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

public abstract class SmartSurveyHelper {

    private static final String CONTACT_TYPE = "contact_type";
    private static final String VRM = "vrm";
    private static final String VEHICLE_TYPE = "vehicle_type";
    private static final String IS_SIGNING_BEFORE_FIRST_MOT_DUE = "is_signing_before_first_mot_due";

    private final String templateVariable;
    private final String uri;
    private final Map<String, String> attributes;

    public SmartSurveyHelper(String templateVariable, String uri) {

        this.templateVariable = templateVariable;
        this.uri = uri;
        this.attributes = new HashMap<>();
    }

    public final void addContactType(String contactType) {

        this.putAttribute(CONTACT_TYPE, contactType);
    }

    public final void addVrm(String vrm) {

        this.putAttribute(VRM, vrm);
    }

    public final void addVehicleType(VehicleType vehicleType) {

        this.putAttribute(VEHICLE_TYPE, vehicleType.toString());
    }

    public final void addIsSigningBeforeFirstMotDue(boolean isSigningBeforeFirstMotDue) {

        this.putAttribute(IS_SIGNING_BEFORE_FIRST_MOT_DUE, String.valueOf(isSigningBeforeFirstMotDue));
    }

    public final Map<String, String> formatAttributes() {

        if (attributes.isEmpty()) {
            return Collections.emptyMap();
        }
        UriBuilder builder = UriBuilder.fromUri(uri);

        Iterator entries = attributes.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry)entries.next();
            builder.queryParam((String)entry.getKey(), (String)entry.getValue());
        }

        Map<String, String> map = new HashMap<>();
        map.put(templateVariable, builder.build().toString());
        return map;
    }

    public final void clear() {

        attributes.clear();
    }

    private void putAttribute(String key, String value) {

        attributes.put(key, value);
    }
}