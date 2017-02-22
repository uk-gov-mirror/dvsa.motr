package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.SubscriptionConfirmationViewModel;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SubscriptionConfirmationResourceTest {

    private static final VehicleDetailsClient VEHICLE_DETAILS_CLIENT = mock(VehicleDetailsClient.class);
    private static final MotrSession MOTR_SESSION = mock(MotrSession.class);
    private static final String VRM = "TEST-REG";

    private TemplateEngineStub templateEngineStub;
    private SubscriptionConfirmationResource resource;

    @Before
    public void setUp() {

        templateEngineStub = new TemplateEngineStub();
        resource = new SubscriptionConfirmationResource(VEHICLE_DETAILS_CLIENT, templateEngineStub, MOTR_SESSION);
    }

    @Test
    public void getResultsInSubscriptionConfirmationTemplate() throws VehicleDetailsClientException, URISyntaxException {

        when(MOTR_SESSION.getRegNumberFromSession()).thenReturn(VRM);
        when(MOTR_SESSION.isAllowedOnPage()).thenReturn(true);

        when(VEHICLE_DETAILS_CLIENT.fetch(VRM)).thenReturn(vehicleDetailsResponse());

        resource.subscriptionConfirmationGet();

        assertEquals("subscription-confirmation", templateEngineStub.getTemplate());
        assertEquals(SubscriptionConfirmationViewModel.class, templateEngineStub.getContext(Map.class).get("viewModel").getClass());
    }

    private Optional<VehicleDetails> vehicleDetailsResponse() {

        VehicleDetails vehicleDetails = new VehicleDetails();

        vehicleDetails.setMake("test-make");
        vehicleDetails.setModel("test-model");
        vehicleDetails.setYearOfManufacture(2000);
        return Optional.of(vehicleDetails);
    }
}
