package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;
import uk.gov.dvsa.motr.ui.base.PageIdentityVerificationException;

@GotoUrl("/unsubscribe/{0}")
public class UnsubscribeErrorPage extends Page {

    @FindBy(className = "heading-xlarge")
    private WebElement headerTitle;

    @FindBy(id = "error-message")
    private WebElement errorMessage;

    @Override
    protected void selfVerify() {

        if (!getHeaderTitle().contains(getContentHeader()) || !this.driver.getTitle().equals(getPageTitle())) {

            throw new PageIdentityVerificationException("Page identity verification failed: "
                    + String.format("\n Expected: %s page, \n Found: %s page", getContentHeader(), getHeaderTitle())
            );
        }
    }

    @Override
    protected String getContentHeader() {

        return "No MOT reminder found";
    }

    @Override
    protected String getPageTitle() {

        return "No MOT reminder found – MOT reminders";
    }

    public String getHeaderTitle() {

        return headerTitle.getText();
    }

    public String getErrorMessageText() {

        return errorMessage.getText();
    }
}
