package uk.gov.dvsa.motr.notifier.notify;

import org.junit.Assert;
import org.junit.Test;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public class MotHistoryUrlFormatterTest {
    private static final String CHECKSUM_SALT = "SOMESALT";
    private static final String EXPECTED_CHECKSUM = "8b536c0fbb5bcee2";
    private static final String MOTH_DIRECT_URL_PREFIX = "http://gov.uk/";

    @Test
    public void testUrlGeneration() throws NoSuchAlgorithmException {
        LocalDate vehicleExpiryDate = LocalDate.of(2017, 10, 10);

        String url = MotHistoryUrlFormatter.getUrl(MOTH_DIRECT_URL_PREFIX, vehicleDetailsStub(vehicleExpiryDate), CHECKSUM_SALT);

        Assert.assertEquals(MOTH_DIRECT_URL_PREFIX + "TEST-VRM" + "/" + EXPECTED_CHECKSUM, url);
    }

    private VehicleDetails vehicleDetailsStub(LocalDate expiryDate) {
        return new VehicleDetails().setMotExpiryDate(expiryDate).setRegNumber("TEST-VRM").setMake("TEST-MAKE").setModel("TEST-MODEL");
    }
}
