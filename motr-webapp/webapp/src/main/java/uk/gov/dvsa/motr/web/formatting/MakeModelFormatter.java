package uk.gov.dvsa.motr.web.formatting;

import com.amazonaws.util.StringUtils;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;

public class MakeModelFormatter {

    public static String getMakeModelString(String make, String model) {

        boolean makeValid = !StringUtils.isNullOrEmpty(make);
        boolean modelValid = !StringUtils.isNullOrEmpty(model);

        if (!makeValid && !modelValid) {
            return "";
        }
        if (makeValid && !modelValid) {
            return make.toUpperCase();
        }
        if (!makeValid && modelValid) {
            return model.toUpperCase();
        }

        return (make + " " + model).toUpperCase();
    }

    public static String getMakeModelDisplayStringFromVehicleDetails(VehicleDetails vehicleDetails, String endDelimeter) {

        if (vehicleDetails == null) {
            return "";
        }

        String makeModel = MakeModelFormatter.getMakeModelString(vehicleDetails.getMake(), vehicleDetails.getModel());

        if (endDelimeter == null) {
            return makeModel;
        }
        return makeModel.equals("") ? makeModel : makeModel + endDelimeter;
    }

}
