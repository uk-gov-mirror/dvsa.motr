package uk.gov.dvsa.motr.journey;

import org.testng.annotations.Test;

import uk.gov.dvsa.motr.base.BaseTest;
import uk.gov.dvsa.motr.navigation.PageNavigator;
import uk.gov.dvsa.motr.ui.page.EmailPage;
import uk.gov.dvsa.motr.ui.page.HomePage;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

public class MotReminderTests extends BaseTest {

    @Test
    public void enteringBaseUrlWillDisplayHomePage() throws IOException {

        driver.loadBaseUrl();
        HomePage page = new HomePage();
        assertTrue(page.isStartNowVisible());
    }

    @Test
    public void enteringEmailUrlWillDisplayEmailPage() throws IOException {

        EmailPage page = PageNavigator.goTo(EmailPage.class);
        assertTrue(page.isEmailContinueButtonDisplayed());
    }
}
