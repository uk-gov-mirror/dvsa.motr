package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/unsubscribe/{0}")
public class UnsubscribeErrorPage extends Page {

    @FindBy(id = "error-message")
    private WebElement errorMessage;

    @Override
    protected String getContentHeader() {

        return "No MOT reminder found";
    }

    @Override
    protected String getPageTitle() {

        return "No MOT reminder found – MOT reminders";
    }

    public String getErrorMessageText() {

        return errorMessage.getText();
    }
}
