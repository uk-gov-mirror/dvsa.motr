package uk.gov.dvsa.motr.web.component.subscription.helper;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import static org.junit.Assert.assertEquals;

public class UnsubscriptionUrlHelperTest {

    private static final String baseUri = "http://test-url/";
    private UnsubscriptionUrlHelper urlHelper;

    @Before
    public void setup() {
        this.urlHelper = new UnsubscriptionUrlHelper(baseUri);
    }

    @Test
    public void buildReturnsCorrectUri() throws Exception {

        Subscription sub = new Subscription().setUnsubscribeId("AAAA").setEmail("email").setVrm("vrm");

        assertEquals("http://test-url/unsubscribe/AAAA", urlHelper.build(sub));
    }
}
