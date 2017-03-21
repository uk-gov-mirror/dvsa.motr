package uk.gov.dvsa.motr.helper;

import uk.gov.dvsa.motr.navigation.PageNavigator;
import uk.gov.dvsa.motr.ui.page.CookiesPage;
import uk.gov.dvsa.motr.ui.page.EmailConfirmationPendingPage;
import uk.gov.dvsa.motr.ui.page.EmailPage;
import uk.gov.dvsa.motr.ui.page.HomePage;
import uk.gov.dvsa.motr.ui.page.ReviewPage;
import uk.gov.dvsa.motr.ui.page.SubscriptionConfirmationPage;
import uk.gov.dvsa.motr.ui.page.TermsAndConditionsPage;
import uk.gov.dvsa.motr.ui.page.UnsubscribePage;
import uk.gov.dvsa.motr.ui.page.VrmPage;

public class MotReminder {

    public DynamoDbSubscriptionHelper subscriptionDb = new DynamoDbSubscriptionHelper();

    public ReviewPage enterReminderDetails(String vrm, String email) {
        HomePage page = PageNavigator.goTo(HomePage.class);
        VrmPage vrmPage = page.clickStartNow();
        EmailPage emailPage = vrmPage.enterVrm(vrm);
        return emailPage.enterEmailAddress(email);
    }

    public EmailConfirmationPendingPage enterReminderDetailsAndConfirm(String vrm, String email){
        ReviewPage reviewPage = enterReminderDetails(vrm, email);
        return reviewPage.confirmSubscriptionDetails();
    }

    public CookiesPage clickCookiesLink() {
        HomePage page = PageNavigator.goTo(HomePage.class);
        return page.clickCookiesLink();
    }

    public TermsAndConditionsPage clickTermsAndConditionsLink() {
        HomePage page = PageNavigator.goTo(HomePage.class);
        return page.clickTermsAndConditionsLink();
    }

    public UnsubscribePage navigateToUnsubscribe(String email, String vrm) {

        String unsubscribeId = subscriptionDb.findUnsubscribeIdByVrmAndEmail(vrm, email);
        return PageNavigator.goTo(UnsubscribePage.class, unsubscribeId);
    }

    public SubscriptionConfirmationPage navigateToEmailConfirmationPage(String email, String vrm) {

        String confirmationId = subscriptionDb.findConfirmationIdByVrmAndEmail(vrm, email);
        return PageNavigator.goTo(SubscriptionConfirmationPage.class, confirmationId);
    }
}
