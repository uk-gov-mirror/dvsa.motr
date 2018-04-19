package uk.gov.dvsa.motr.web.viewmodel;

import com.amazonaws.util.StringUtils;

import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.formatting.DateFormatter;

import java.time.LocalDate;

public class ReviewViewModel {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ReviewViewModel.class);

    private static String UNKNOWN_STRING = "UNKNOWN";

    private String registration;
    private String contact;
    private String make;
    private String model;
    private String makeInfull;
    private String colour;
    private String yearOfManufacture;
    private LocalDate expiryDate;
    private boolean dvlaVehicle; // TODO verify whether we can remove that and use hasTests instead even for MOT vehicles
    private boolean emailChannel;
    private boolean mobileChannel;
    private VehicleType vehicleType;
    private boolean hasTests;

    public String getRegistration() {

        return registration;
    }

    public ReviewViewModel setRegistration(String registration) {

        this.registration = registration;
        return this;
    }

    public String getContact() {

        return contact;
    }

    public ReviewViewModel setContact(String contact) {

        this.contact = contact;
        return this;
    }

    public String getColour() {

        return colour;
    }

    public ReviewViewModel setColour(String colour, String colourSecondary) {

        if (colour == null || "".equals(colour)) {
            this.colour = UNKNOWN_STRING;
            return this;
        }

        if (colourSecondary == null || "".equals(colourSecondary)) {
            this.colour = colour.toUpperCase();
        } else {
            String colours = colour + ", " + colourSecondary;
            this.colour = colours.toUpperCase();
        }

        return this;
    }

    public String getYearOfManufacture() {

        return yearOfManufacture;
    }

    public ReviewViewModel setYearOfManufacture(String yearOfManufacture) {

        if (yearOfManufacture == null || "".equals(yearOfManufacture)) {
            this.yearOfManufacture = UNKNOWN_STRING;
            return this;
        }
        this.yearOfManufacture = yearOfManufacture;
        return this;
    }

    public String getExpiryDate() {

        if (expiryDate == null) {
            return UNKNOWN_STRING;
        }

        return DateFormatter.asDisplayDate(expiryDate);
    }

    public ReviewViewModel setExpiryDate(LocalDate expiryDate) {

        this.expiryDate = expiryDate;
        return this;
    }

    public boolean isDvlaVehicle() {

        return dvlaVehicle;
    }

    public ReviewViewModel setDvlaVehicle(boolean dvlaVehicle) {

        logger.info("ReviewViewModel setDvlaVehicle to: " + dvlaVehicle);
        this.dvlaVehicle = dvlaVehicle;
        return this;
    }

    public String getMake() {

        if (useMakeInFull()) {
            return makeInfull.toUpperCase();
        }
        if (!StringUtils.isNullOrEmpty(make)) {
            return make.toUpperCase();
        }

        return UNKNOWN_STRING;
    }

    public ReviewViewModel setMake(String make) {

        this.make = make;
        return this;
    }

    public String getModel() {

        if (useMakeInFull()) {
            return "";
        }

        if (!StringUtils.isNullOrEmpty(model)) {
            return model.toUpperCase();
        }
        return UNKNOWN_STRING;
    }

    public ReviewViewModel setModel(String model) {

        this.model = model;
        return this;
    }

    public String getMakeInfull() {

        return makeInfull.toUpperCase();
    }

    public ReviewViewModel setMakeInFull(String makeInFull) {

        this.makeInfull = makeInFull;
        return this;
    }

    private boolean useMakeInFull() {

        if ((StringUtils.isNullOrEmpty(make) || make.equalsIgnoreCase(UNKNOWN_STRING))
                && (StringUtils.isNullOrEmpty(model) || model.equalsIgnoreCase(UNKNOWN_STRING))
                && !StringUtils.isNullOrEmpty(makeInfull)) {

            return true;
        }
        return false;
    }

    public boolean isEmailChannel() {

        return emailChannel;
    }

    public ReviewViewModel setEmailChannel(boolean emailChannel) {

        this.emailChannel = emailChannel;
        return this;
    }

    public boolean isMobileChannel() {

        return mobileChannel;
    }

    public ReviewViewModel setMobileChannel(boolean mobileChannel) {

        this.mobileChannel = mobileChannel;
        return this;
    }

    public ReviewViewModel setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
        return this;
    }

    public ReviewViewModel setHasTests(boolean hasTests) {
        this.hasTests = hasTests;
        return this;
    }

    public String getExpiryDateLabelText() {
        if (vehicleType == null || VehicleType.MOT.equals(vehicleType)) {
            if (this.isDvlaVehicle()) {
                return "MOT due date";
            }

            return "MOT expiry date";
        } else {
            if (this.hasTests) {
                return "Annual test expiry date";
            }

            return "Annual test due date";
        }
    }
}
