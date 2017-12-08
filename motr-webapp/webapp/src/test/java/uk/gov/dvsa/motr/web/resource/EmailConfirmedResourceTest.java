package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailConfirmedResourceTest {

    private static final String CONFIRMATION_ID = "ABC123";
    private static final String CONFIRM_SUBSCRIPTION_URI = "CONFIRM-SUBSCRIPTION-URI";

    private EmailConfirmedResource resource;
    private UrlHelper urlHelper = mock(UrlHelper.class);

    @Before
    public void setup() {

        resource = new EmailConfirmedResource(urlHelper);
        when(urlHelper.confirmSubscriptionLink(CONFIRMATION_ID)).thenReturn(CONFIRM_SUBSCRIPTION_URI);
    }

    @Test
    public void emailConfirmedResourceRedirectsToSubscriptionConfirmedOnGet() throws Exception {

        Response response = resource.confirmSubscriptionGet(CONFIRMATION_ID);

        assertEquals(302, response.getStatus());
        assertEquals(CONFIRM_SUBSCRIPTION_URI, response.getLocation().toString());
    }
}
