package uk.gov.dvsa.motr.web.component.subscription.helper;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;

import static org.junit.Assert.assertEquals;

public class EmailConfirmationUrlHelperTest {

    private static final String baseUri = "http://test-url/";
    private EmailConfirmationUrlHelper urlHelper;

    @Before
    public void setup() {
        this.urlHelper = new EmailConfirmationUrlHelper(baseUri);
    }

    @Test
    public void buildReturnsCorrectUri() throws Exception {

        PendingSubscription sub = new PendingSubscription().setConfirmationId("AAAA").setEmail("email").setVrm("vrm");

        assertEquals("http://test-url/confirm-email/AAAA", urlHelper.build(sub));
    }
}
