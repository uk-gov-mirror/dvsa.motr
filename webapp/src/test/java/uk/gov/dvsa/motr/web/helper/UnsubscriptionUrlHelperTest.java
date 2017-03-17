package uk.gov.dvsa.motr.web.helper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class UnsubscriptionUrlHelperTest {

    private static final String baseUri = "http://test-url/";
    private UnsubscriptionUrlHelper urlHelper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        this.urlHelper = new UnsubscriptionUrlHelper(baseUri);
    }

    @Test
    public void buildReturnsCorrectUri() throws Exception {

        assertEquals(baseUri + "unsubscribe/TEST-ID", urlHelper.build("TEST-ID"));
    }
}
