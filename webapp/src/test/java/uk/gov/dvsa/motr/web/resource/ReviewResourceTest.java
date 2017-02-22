package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReviewResourceTest {

    private static final SubscriptionService SUBSCRIPTION_SERVICE = mock(SubscriptionService.class);
    private static final VehicleDetailsClient VEHICLE_DETAILS_CLIENT = mock(VehicleDetailsClient.class);
    private static final VrmValidator VRM_VALIDATOR = mock(VrmValidator.class);
    private static final EmailValidator EMAIL_VALIDATOR = mock(EmailValidator.class);
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
                SUBSCRIPTION_SERVICE,
                VEHICLE_DETAILS_CLIENT
        );
        when(MOTR_SESSION.getRegNumberFromSession()).thenReturn(VRM);
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

    @Test
    public void redirectsToSubscriptionScreenWhenReviewDetailsValid() throws Exception {

        when(VRM_VALIDATOR.isValid(VRM)).thenReturn(true);
        when(EMAIL_VALIDATOR.isValid(EMAIL)).thenReturn(true);
        when(VEHICLE_DETAILS_CLIENT.fetch(VRM)).thenReturn(vehicleDetailsResponse());
        doNothing().when(SUBSCRIPTION_SERVICE).createSubscription(anyString(), anyString(), any());
        Response actual = resource.reviewPagePost();
        assertEquals(302, actual.getStatus());
    }

    @Test(expected = Exception.class)
    public void throwsNotFoundOnReviewScreenWhenSubscriptionAlreadyExists() throws Exception {

        when(VRM_VALIDATOR.isValid(VRM)).thenReturn(true);
        when(EMAIL_VALIDATOR.isValid(EMAIL)).thenReturn(true);
        when(VEHICLE_DETAILS_CLIENT.fetch(VRM)).thenReturn(vehicleDetailsResponse());
        doThrow(SubscriptionAlreadyExistsException.class).when(SUBSCRIPTION_SERVICE).createSubscription(anyString(), anyString(), any());
        Response actual = resource.reviewPagePost();
        assertEquals(404, actual.getStatus());
    }

    @Test(expected = NotFoundException.class)
    public void whenNoVehicleReturnedFromApiNotFoundThrown() throws Exception {

        when(MOTR_SESSION.isAllowedOnPage()).thenReturn(true);
        when(VEHICLE_DETAILS_CLIENT.fetch(VRM)).thenReturn(Optional.empty());
        resource.reviewPage();
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
