package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/review")
public class ReviewPage extends Page {

    @FindBy(id = "change-registration-link")
    private WebElement changeRegistrationLink;
    @FindBy(id = "change-email-link")
    private WebElement changeEmailLink;
    @FindBy(id = "continue-button")
    private WebElement continueButton;

    @Override
    protected String getIdentity() {
        return "Check your details";
    }

    public boolean isContinueButtonDisplayed() {
        return continueButton.isDisplayed();
    }
}
