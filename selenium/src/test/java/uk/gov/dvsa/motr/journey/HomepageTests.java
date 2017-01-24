package uk.gov.dvsa.motr.journey;

import org.testng.annotations.Test;

import uk.gov.dvsa.motr.base.BaseTest;
import uk.gov.dvsa.motr.ui.page.Homepage;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

public class HomepageTests extends BaseTest {

    @Test
    public void enteringBaseUrlWillLoadHomepage() throws IOException {

        driver.loadBaseUrl();
        Homepage page = new Homepage();
        assertTrue(page.isStartNowVisible());
    }
}