package uk.gov.dvsa.motr.journey;

import org.testng.annotations.Test;

import uk.gov.dvsa.motr.base.BaseTest;
import uk.gov.dvsa.motr.helper.MotReminder;
import uk.gov.dvsa.motr.helper.RandomGenerator;
import uk.gov.dvsa.motr.ui.page.EmailPage;
import uk.gov.dvsa.motr.ui.page.ReviewPage;
import uk.gov.dvsa.motr.ui.page.SubscriptionConfirmationPage;
import uk.gov.dvsa.motr.ui.page.VrmPage;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class MotReminderTests extends BaseTest {

    @Test(description = "Owner of a vehicle with a mot is able to set up a MOT reminder with their VRM and email")
    public void createMotReminderForMyVehicle() throws IOException, InterruptedException {

        //Given I am a vehicle owner on the MOTR start page
        //When I enter the vehicle vrm and my email address
        ReviewPage reviewPage = MotReminder.enterReminderDetails(RandomGenerator.generateVrm(), RandomGenerator.generateEmail());
        SubscriptionConfirmationPage confirmPage = reviewPage.confirmSubscriptionDetails();

        //Then my mot reminder is set up successfully
        assertEquals(confirmPage.getHeaderTitle(), "You've signed up for MOT reminders");
    }

    @Test(description = "Owner of a vehicle with a mot can change their email when creating MOT reminder")
    public void canChangeEmailFromReviewWhenCreatingReminder() {

        //Given I am a vehicle owner on the MOTR start page
        //When I enter the vehicle vrm and my email address
        ReviewPage reviewPage = MotReminder.enterReminderDetails(RandomGenerator.generateVrm(), RandomGenerator.generateEmail());

        //And I update my email address
        EmailPage emailPageFromReview = reviewPage.clickChangeEmail();
        ReviewPage reviewPageSubmit = emailPageFromReview.enterEmailAddress(RandomGenerator.generateEmail());

        //Then my mot reminder is set up successfully with the updated email address
        SubscriptionConfirmationPage confirmPage = reviewPageSubmit.confirmSubscriptionDetails();
        assertEquals(confirmPage.getHeaderTitle(), "You've signed up for MOT reminders");
    }

    @Test(description = "Owner of a vehicle with a mot can change their vrm when creating MOT reminder")
    public void canChangeVrmFromReviewWhenCreatingReminder() {

        //Given I am a vehicle owner on the MOTR start page
        //When I enter the vehicle vrm and my email address
        ReviewPage reviewPage = MotReminder.enterReminderDetails(RandomGenerator.generateVrm(), RandomGenerator.generateEmail());

        //And I update my vehicle vrm
        VrmPage vrmPageFromReview = reviewPage.clickChangeVrm();
        ReviewPage reviewPageSubmit = vrmPageFromReview.enterVrmExpectingReturnToReview(RandomGenerator.generateVrm());

        //Then my mot reminder is set up successfully with the updated vehicle vrm
        SubscriptionConfirmationPage confirmPage = reviewPageSubmit.confirmSubscriptionDetails();
        assertEquals(confirmPage.getHeaderTitle(), "You've signed up for MOT reminders");
    }
}
