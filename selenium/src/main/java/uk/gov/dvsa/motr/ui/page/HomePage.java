package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/")
public class HomePage extends Page {

    @FindBy(id = "startButton")
    private WebElement startNowButton;

    @Override
    protected String getIdentity() {
        return "Get reminders about your MOT";
    }

    public VrmPage clickStartNow() {
        startNowButton.click();
        return new VrmPage();
    }
}
