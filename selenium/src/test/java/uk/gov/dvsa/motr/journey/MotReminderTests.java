package uk.gov.dvsa.motr.journey;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import uk.gov.dvsa.motr.base.BaseTest;
import uk.gov.dvsa.motr.helper.RandomGenerator;
import uk.gov.dvsa.motr.ui.page.CookiesPage;
import uk.gov.dvsa.motr.ui.page.EmailConfirmationPendingPage;
import uk.gov.dvsa.motr.ui.page.EmailPage;
import uk.gov.dvsa.motr.ui.page.ReviewPage;
import uk.gov.dvsa.motr.ui.page.SubscriptionConfirmationPage;
import uk.gov.dvsa.motr.ui.page.TermsAndConditionsPage;
import uk.gov.dvsa.motr.ui.page.UnsubscribeConfirmationPage;
import uk.gov.dvsa.motr.ui.page.UnsubscribePage;
import uk.gov.dvsa.motr.ui.page.VrmPage;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class MotReminderTests extends BaseTest {

    @Test(dataProvider = "dataProviderCreateMotReminderForMyVehicle",
            description = "Owner of a vehicle with a mot is able to set up a MOT reminder with their VRM and email and unsubscribe from it")
    public void createMotReminderForMyVehicleThenUnsubscribe(String vrm, String email) throws IOException, InterruptedException {

        //Given I am a vehicle owner on the MOTR start page
        //When I enter the vehicle vrm and my email address
        motReminder.enterReminderDetailsAndConfirm(vrm, email);

        // And I confirm my email address
        SubscriptionConfirmationPage confirmationPage = motReminder.navigateToEmailConfirmationPage(email, vrm);
        confirmationPage.areDisplayedDetailsCorrect(email, vrm);

        //When I select to unsubscribe from an email reminder
        String subscriptionId = motReminder.subscriptionDb.findSubscriptionIdByVrmAndEmail(vrm, email);
        UnsubscribePage unsubscribe = motReminder.navigateToUnsubscribe(subscriptionId);
        //And confirm that I would like to unsubscribe
        UnsubscribeConfirmationPage unsubscribeConfirmed = unsubscribe.clickUnsubscribe();

        //Then my MOT reminder subscription has been cancelled
        assertEquals(unsubscribeConfirmed.getBannerTitle(), "You’ve unsubscribed");
    }

    @Test(description = "Owner of a vehicle with a mot can change their email when creating MOT reminder")
    public void canChangeEmailFromReviewWhenCreatingReminder() {

        //Given I am a vehicle owner on the MOTR start page
        //When I enter the vehicle vrm and my email address
        ReviewPage reviewPage = motReminder.enterReminderDetails(RandomGenerator.generateVrm(), RandomGenerator.generateEmail());

        //And I update my email address
        EmailPage emailPageFromReview = reviewPage.clickChangeEmail();
        ReviewPage reviewPageSubmit = emailPageFromReview.enterEmailAddress(RandomGenerator.generateEmail());

        //Then my mot reminder is set up successfully with the updated email address
        EmailConfirmationPendingPage confirmPage = reviewPageSubmit.confirmSubscriptionDetails();
        assertEquals(confirmPage.getTitle(), "You’ve nearly finished");
    }

    @Test(description = "Owner of a vehicle with a mot can change their vrm when creating MOT reminder")
    public void canChangeVrmFromReviewWhenCreatingReminder() {

        //Given I am a vehicle owner on the MOTR start page
        //When I enter the vehicle vrm and my email address
        ReviewPage reviewPage = motReminder.enterReminderDetails(RandomGenerator.generateVrm(), RandomGenerator.generateEmail());

        //And I update my vehicle vrm
        VrmPage vrmPageFromReview = reviewPage.clickChangeVrm();
        ReviewPage reviewPageSubmit = vrmPageFromReview.enterVrmExpectingReturnToReview(RandomGenerator.generateVrm());

        //Then my mot reminder is set up successfully with the updated vehicle vrm
        EmailConfirmationPendingPage confirmPage = reviewPageSubmit.confirmSubscriptionDetails();
        assertEquals(confirmPage.getTitle(), "You’ve nearly finished");
    }

    @Test(description = "Vehicle owner with an MOT reminder subscription can unsubscribe from the service")
    public void unsubscribeFromMotReminders() {

        //Given I have signed up for the MOT reminder service
        String id = motReminder.subscriptionDb.addSubscription("SELENIUM-VRM", "SELENIUM@EMAIL.COM");

        //When I select to unsubscribe from an email reminder
        UnsubscribePage unsubscribe = motReminder.navigateToUnsubscribe(id);

        //And confirm that I would like to unsubscribe
        UnsubscribeConfirmationPage unsubscribeConfirmed = unsubscribe.clickUnsubscribe();

        //Then my MOT reminder subscription has been cancelled
        assertEquals(unsubscribeConfirmed.getBannerTitle(), "You’ve unsubscribed");
    }

    @Test(description = "As a user of the site with a vested interest in cookie policy, I can view them")
    public void canViewCookiesPageWhenClickingLinkInFooter() {

        //Given I am a user of the site
        //When I click the cookies link in footer of the page
        CookiesPage cookiesPage = motReminder.clickCookiesLink();

        //Then I am taken to the cookies page
        assertEquals(cookiesPage.getTitle(), "Cookies", "Cookies page is not returned");
    }

    @Test(description = "As a user of the site with a vested interest in terms and conditions of the service, I can view them")
    public void canViewTermsAndConditionsPageWhenClickingLinkInFooter() {

        //Given I am a user of the site
        //When I click the terms and conditions link in footer of the page
        TermsAndConditionsPage termsAndConditionsPage = motReminder.clickTermsAndConditionsLink();

        //Then I am taken to the terms and conditions page
        assertEquals(termsAndConditionsPage.getTitle(), "Terms and conditions", "Terms and conditions page is not returned");
    }


    @DataProvider(name = "dataProviderCreateMotReminderForMyVehicle")
    public Object[][] dataProviderCreateMotReminderForMyVehicle() throws IOException {

        return new Object[][]{{RandomGenerator.generateVrm(), RandomGenerator.generateEmail()}};
    }
}
