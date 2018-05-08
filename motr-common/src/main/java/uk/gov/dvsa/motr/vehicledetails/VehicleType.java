package uk.gov.dvsa.motr.vehicledetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.swing.text.html.Option;
import java.util.Optional;

public enum VehicleType {
    @JsonProperty("HGV")
    HGV,
    @JsonProperty("PSV")
    PSV,
    @JsonProperty("MOT")
    MOT;

    /**
     * Parse VehicleType from string
     *
     * @param value VehicleType string
     * @return corresponding VehicleType
     */
    public static VehicleType getFromString(String value) {
        return Optional.ofNullable(value)
            .filter(v -> !v.isEmpty())
            .map(VehicleType::valueOf)
            .orElse(getDefault());
    }

    /**
      * @return For backward compatibility: all pre-existing subscriptions without vehicle_type field are of the type MOT
     */
    public static VehicleType getDefault() {
        return MOT;
    }
}
