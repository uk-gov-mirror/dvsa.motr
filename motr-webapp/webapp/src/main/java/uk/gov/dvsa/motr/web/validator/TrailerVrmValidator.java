package uk.gov.dvsa.motr.web.validator;

public class TrailerVrmValidator implements Validator {
    public static final String REGISTRATION_EMPTY_MESSAGE = "Enter the vehicle’s registration";
    public static final String TRAILER_A_REGEX = "\\d{6}[6|7]\\d";
    public static final String TRAILER_B_C_REGEX = "[A|C]\\d{6}";

    private String message;

    @Override
    public boolean isValid(String vehicleRegistration) {

        if (vehicleRegistration == null || "".equals(vehicleRegistration)) {
            this.message = REGISTRATION_EMPTY_MESSAGE;
            return false;
        }

        return vehicleRegistration.toUpperCase().matches(TRAILER_A_REGEX)
                || vehicleRegistration.toUpperCase().matches(TRAILER_B_C_REGEX);
    }

    public String getMessage() {

        return this.message;
    }
}
