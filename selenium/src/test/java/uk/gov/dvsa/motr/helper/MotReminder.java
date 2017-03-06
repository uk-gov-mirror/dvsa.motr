package uk.gov.dvsa.motr.helper;

import uk.gov.dvsa.motr.navigation.PageNavigator;
import uk.gov.dvsa.motr.ui.page.CookiesPage;
import uk.gov.dvsa.motr.ui.page.EmailPage;
import uk.gov.dvsa.motr.ui.page.HomePage;
import uk.gov.dvsa.motr.ui.page.ReviewPage;
import uk.gov.dvsa.motr.ui.page.UnsubscribePage;
import uk.gov.dvsa.motr.ui.page.VrmPage;

public class MotReminder {

    public static ReviewPage enterReminderDetails(String vrm, String email) {
        HomePage page = PageNavigator.goTo(HomePage.class);
        VrmPage vrmPage = page.clickStartNow();
        EmailPage emailPage = vrmPage.enterVrm(vrm);
        return emailPage.enterEmailAddress(email);
    }

    public static CookiesPage clickCookiesLink() {
        HomePage page = PageNavigator.goTo(HomePage.class);
        return page.clickCookiesLink();
    }

    public static UnsubscribePage navigateToUnsubscribe(String id) {

        return PageNavigator.goTo(UnsubscribePage.class, id);
    }
}
