package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/")
public class Homepage extends Page {

    @FindBy(id = "startButton")
    private WebElement startNowBtn;

    @Override
    protected String getIdentity() {
        return "Get reminders about your MOT";
    }

    public boolean isStartNowVisible() {
        return startNowBtn.isDisplayed();
    }

}
