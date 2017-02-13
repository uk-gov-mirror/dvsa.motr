package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.ReviewViewModel;

import java.util.Map;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class ReviewResourceTest {

    private static final VehicleDetailsClient VEHICLE_DETAILS_CLIENT = mock(VehicleDetailsClient.class);

    @Test
    public void reviewTemplateIsRenderedOnGetWithViewModel() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        ReviewResource resource = new ReviewResource(engine, VEHICLE_DETAILS_CLIENT);

        when(VEHICLE_DETAILS_CLIENT.fetch("YN13NTX")).thenReturn(vehicleDetailsResponse());

        assertEquals(RESPONSE, resource.reviewPage());
        assertEquals("review", engine.getTemplate());
        assertEquals(ReviewViewModel.class, engine.getContext(Map.class).get("viewModel").getClass());
    }

    @Test(expected = NotFoundException.class)
    public void whenNoVehicleReturnedFromApiNotFoundThrown() throws Exception {
        TemplateEngineStub engine = new TemplateEngineStub();
        ReviewResource resource = new ReviewResource(engine, VEHICLE_DETAILS_CLIENT);

        when(VEHICLE_DETAILS_CLIENT.fetch("YN13NTX")).thenReturn(Optional.empty());
        resource.reviewPage();
    }

    private Optional<VehicleDetails> vehicleDetailsResponse() {
        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMake("make");
        vehicleDetails.setModel("model");
        vehicleDetails.setYearOfManufacture(2000);
        return Optional.of(vehicleDetails);
    }
}
