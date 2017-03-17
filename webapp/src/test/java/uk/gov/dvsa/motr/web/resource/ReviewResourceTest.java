package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.component.subscription.service.PendingSubscriptionService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.ReviewViewModel;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReviewResourceTest {

    private static final PendingSubscriptionService PENDING_SUBSCRIPTION_SERVICE = mock(PendingSubscriptionService.class);
    private static final VehicleDetailsClient VEHICLE_DETAILS_CLIENT = mock(VehicleDetailsClient.class);
    private static final TemplateEngineStub TEMPLATE_ENGINE_STUB = new TemplateEngineStub();
    private static final MotrSession MOTR_SESSION = mock(MotrSession.class);
    private static final String VRM = "YN13NTX";
    private static final String EMAIL = "test@test.com";

    private ReviewResource resource;

    @Before
    public void setUp() {

        this.resource = new ReviewResource(
                MOTR_SESSION,
                TEMPLATE_ENGINE_STUB,
                PENDING_SUBSCRIPTION_SERVICE,
                VEHICLE_DETAILS_CLIENT
        );
        when(MOTR_SESSION.getVrmFromSession()).thenReturn(VRM);
        when(MOTR_SESSION.getEmailFromSession()).thenReturn(EMAIL);
    }

    @Test
    public void reviewTemplateIsRenderedOnGetWithViewModel() throws Exception {

        when(MOTR_SESSION.isAllowedOnPage()).thenReturn(true);
        when(VEHICLE_DETAILS_CLIENT.fetch(VRM)).thenReturn(vehicleDetailsResponse());

        assertEquals(200, resource.reviewPage().getStatus());
        assertEquals("review", TEMPLATE_ENGINE_STUB.getTemplate());
        assertEquals(ReviewViewModel.class, TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel").getClass());
    }

    @Test(expected = NotFoundException.class)
    public void whenNoVehicleReturnedFromApiNotFoundThrown() throws Exception {

        when(MOTR_SESSION.isAllowedOnPage()).thenReturn(true);
        when(VEHICLE_DETAILS_CLIENT.fetch(VRM)).thenReturn(Optional.empty());
        resource.reviewPage();
    }

    @Test// TODO
    public void userIsRedirectedAfterSuccessfullFormSubmission() throws Exception {

        LocalDate now = LocalDate.now();
        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMotExpiryDate(now);

        when(VEHICLE_DETAILS_CLIENT.fetch(VRM)).thenReturn(Optional.of(vehicleDetails));
        doNothing().when(PENDING_SUBSCRIPTION_SERVICE).createPendingSubscription(VRM, EMAIL, now);

        Response response = resource.confirmationPagePost();

        verify(PENDING_SUBSCRIPTION_SERVICE, times(1)).createPendingSubscription(VRM, EMAIL, now);
        assertEquals(302, response.getStatus());
        assertEquals("email-confirmation-pending", response.getLocation().toString());
    }

    private Optional<VehicleDetails> vehicleDetailsResponse() {
        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMake("make");
        vehicleDetails.setModel("model");
        vehicleDetails.setYearOfManufacture(2000);
        vehicleDetails.setMotExpiryDate(LocalDate.now());
        return Optional.of(vehicleDetails);
    }
}
