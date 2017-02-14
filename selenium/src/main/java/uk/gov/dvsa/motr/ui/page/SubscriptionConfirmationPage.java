package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;
import uk.gov.dvsa.motr.ui.base.PageIdentityVerificationException;

@GotoUrl("/subscription-confirmation")
public class SubscriptionConfirmationPage extends Page {

    @FindBy(className = "transaction-header__title")
    private WebElement headerTitle;

    @Override
    protected void selfVerify() {

        if (!getHeaderTitle().contains(getIdentity())) {

            throw new PageIdentityVerificationException("Page identity verification failed: "
                    + String.format("\n Expected: %s page, \n Found: %s page", getIdentity(), getHeaderTitle())
            );
        }
    }

    @Override
    protected String getIdentity() {

        return "You've signed up for MOT reminders";
    }

    private String getHeaderTitle() {

        return headerTitle.getText();
    }
}
