package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/email")
public class EmailPage extends Page {

    @FindBy(id = "emailAddress")
    private WebElement emailAddressField;
    @FindBy(id = "continue")
    private WebElement continueButton;

    @Override
    protected String getIdentity() {
        return "What is your email address?";
    }

    public boolean isEmailContinueButtonDisplayed() {
        return continueButton.isDisplayed();
    }
}
