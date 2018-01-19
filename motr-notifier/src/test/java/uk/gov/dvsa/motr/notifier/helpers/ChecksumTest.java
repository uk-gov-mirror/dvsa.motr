package uk.gov.dvsa.motr.notifier.helpers;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ChecksumTest {

    private static final String CHECKSUM_VRM = "ABC123";
    private static final String CHECKSUM_SALT = "SOMESALT";
    private static final String CHECKSUM_EXPECTED = "962deda8ec46e020";
    private static final String INVALID_CHECKSUM_VRM = "ZXY321";

    private VehicleDetails vehicleDetails;

    @Before
    public void setUp() {

        this.vehicleDetails = new VehicleDetails();
        this.vehicleDetails.setRegNumber(CHECKSUM_VRM);
    }

    @Test
    public void verifyChecksumIsGeneratedAndCorrect() throws NoSuchAlgorithmException {

        String checksum = Checksum.generate(this.vehicleDetails, CHECKSUM_SALT);

        assertEquals(checksum.length(), Checksum.CHECKSUM_LENGTH);
        assertEquals(CHECKSUM_EXPECTED, checksum);
    }

    @Test
    public void verifyInvalidChecksumIsGenerated() throws NoSuchAlgorithmException {

        this.vehicleDetails.setRegNumber(INVALID_CHECKSUM_VRM);

        String checksum = Checksum.generate(this.vehicleDetails, CHECKSUM_SALT);

        assertEquals(checksum.length(), Checksum.CHECKSUM_LENGTH);
        assertNotEquals(CHECKSUM_EXPECTED, checksum);
    }

}
