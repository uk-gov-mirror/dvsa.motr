package uk.gov.dvsa.motr.notifier.helpers;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringJoiner;

import javax.xml.bind.DatatypeConverter;

public class Checksum {

    public static final int CHECKSUM_LENGTH = 16;

    public static String generate(VehicleDetails vehicleDetails, String salt) throws NoSuchAlgorithmException {

        final MessageDigest digest = MessageDigest.getInstance("SHA-256");

        StringJoiner stringJoiner = new StringJoiner("");
        stringJoiner.add(vehicleDetails.getRegNumber());
        stringJoiner.add(salt);

        digest.update(stringJoiner.toString().getBytes());
        String encryptedHex = DatatypeConverter.printHexBinary(digest.digest()).toLowerCase();

        return encryptedHex.substring(0, CHECKSUM_LENGTH);
    }
}
