package uk.gov.dvsa.motr.notifier.notify;

import uk.gov.dvsa.motr.notifier.helpers.Checksum;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

import java.security.NoSuchAlgorithmException;

public class MotHistoryUrlFormatter {

    public static String getUrl(String mothDirectUrlPrefix, VehicleDetails vehicleDetails, String checksumSalt)
            throws NoSuchAlgorithmException {

        String checksum;
        checksum = Checksum.generate(vehicleDetails, checksumSalt);

        StringBuilder mothDirectUrl = new StringBuilder();
        mothDirectUrl.append(mothDirectUrlPrefix);
        mothDirectUrl.append(vehicleDetails.getRegNumber());
        mothDirectUrl.append("/");
        mothDirectUrl.append(checksum);
        return mothDirectUrl.toString();
    }

}
