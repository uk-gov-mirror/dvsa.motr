package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionService;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.validator.EmailValidator;
import uk.gov.dvsa.motr.web.validator.VrmValidator;
import uk.gov.dvsa.motr.web.viewmodel.ReviewViewModel;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class ReviewResourceTest {

    private static final SubscriptionService SUBSCRIPTION_SERVICE = mock(SubscriptionService.class);
    private static final VehicleDetailsClient VEHICLE_DETAILS_CLIENT = mock(VehicleDetailsClient.class);
    private static final VrmValidator VRM_VALIDATOR = mock(VrmValidator.class);
    private static final EmailValidator EMAIL_VALIDATOR = mock(EmailValidator.class);
    private static final TemplateEngineStub TEMPLATE_ENGINE_STUB = new TemplateEngineStub();
    private static final NotifyService NOTIFY_SERVICE = mock(NotifyService.class);
    private static final String BASE_URL = "https://testUrl";

    @Test
    public void reviewTemplateIsRenderedOnGetWithViewModel() throws Exception {

        ReviewResource resource = new ReviewResource(TEMPLATE_ENGINE_STUB, SUBSCRIPTION_SERVICE, VEHICLE_DETAILS_CLIENT, BASE_URL);

        when(VEHICLE_DETAILS_CLIENT.fetch("YN13NTX")).thenReturn(vehicleDetailsResponse());

        assertEquals(RESPONSE, resource.reviewPage());
        assertEquals("review", TEMPLATE_ENGINE_STUB.getTemplate());
        assertEquals(ReviewViewModel.class, TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel").getClass());
    }

    @Test
    public void redirectsToSubscriptionScreenWhenReviewDetailsValid() throws Exception {

        ReviewResource resource = new ReviewResource(TEMPLATE_ENGINE_STUB, SUBSCRIPTION_SERVICE, VEHICLE_DETAILS_CLIENT,
                BASE_URL);

        when(VRM_VALIDATOR.isValid("test-reg")).thenReturn(true);
        when(EMAIL_VALIDATOR.isValid(any())).thenReturn(true);
        when(VEHICLE_DETAILS_CLIENT.fetch("test-reg")).thenReturn(Optional.of(mockVehicleDetails()));
        Response actual = resource.reviewPagePost();
        assertEquals(302, actual.getStatus());
    }

    private VehicleDetails mockVehicleDetails() {

        VehicleDetails vehicleDetails = new VehicleDetails();

        vehicleDetails.setModel("TestModel");
        vehicleDetails.setMake("TestMake");
        vehicleDetails.setMotExpiryDate(LocalDate.now());
        vehicleDetails.setPrimaryColour("Pink");
        vehicleDetails.setSecondaryColour("Not stated");
        vehicleDetails.setRegNumber("test-reg");
        vehicleDetails.setYearOfManufacture(2001);

        return vehicleDetails;
    }

    @Test(expected = NotFoundException.class)
    public void whenNoVehicleReturnedFromApiNotFoundThrown() throws Exception {
        TemplateEngineStub engine = new TemplateEngineStub();
        ReviewResource resource = new ReviewResource(TEMPLATE_ENGINE_STUB, SUBSCRIPTION_SERVICE, VEHICLE_DETAILS_CLIENT,
                BASE_URL);
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
