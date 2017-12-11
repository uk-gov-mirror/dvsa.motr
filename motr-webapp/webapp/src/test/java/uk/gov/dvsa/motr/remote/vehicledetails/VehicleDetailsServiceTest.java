package uk.gov.dvsa.motr.remote.vehicledetails;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClientException;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class VehicleDetailsServiceTest {

    private static final VehicleDetailsClient vehicleDetailsClientMock = mock(VehicleDetailsClient.class);

    @Before
    public void setuo() {
        reset(vehicleDetailsClientMock);
    }

    @Test
    public void whenClientCalledWithVrm_ThenFetchIsCalledWithVrmAsParam() throws VehicleDetailsClientException {

        String testVrm = "ABCDEF";

        when(vehicleDetailsClientMock.fetchByVrm(testVrm)).thenReturn(getMockVehicleDetails());

        VehicleDetails vehicleDetails = VehicleDetailsService.getVehicleDetails(testVrm, vehicleDetailsClientMock);

        assert vehicleDetails != null;
        assertEquals("testMake", vehicleDetails.getMake());
        assertEquals("testModel", vehicleDetails.getModel());
    }

    @Test
    public void whenClientCalledWithUnknownVrm_ThenNullReturned() throws VehicleDetailsClientException {

        String testVrm = "ABCDEF";

        when(vehicleDetailsClientMock.fetchByVrm(testVrm)).thenThrow(new VehicleDetailsClientException(""));

        VehicleDetails vehicleDetails = VehicleDetailsService.getVehicleDetails(testVrm, vehicleDetailsClientMock);

        assertNull(vehicleDetails);
    }

    private Optional<VehicleDetails> getMockVehicleDetails() {
        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMake("testMake");
        vehicleDetails.setModel("testModel");
        return Optional.of(vehicleDetails);
    }

}
