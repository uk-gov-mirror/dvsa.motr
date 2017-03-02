package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;
import uk.gov.dvsa.motr.ui.base.PageIdentityVerificationException;

@GotoUrl("/unsubscribe/confirm")
public class UnsubscribeConfirmationPage extends Page{

    @FindBy(className = "banner__heading-large")
    private WebElement bannerTitle;

    @Override
    protected void selfVerify() {

        if (!getBannerTitle().contains(getIdentity())) {

            throw new PageIdentityVerificationException("Page identity verification failed: "
                    + String.format("\n Expected: %s page, \n Found: %s page", getIdentity(), getBannerTitle())
            );
        }
    }

    @Override
    protected String getIdentity() {

        return "You’ve unsubscribed";
    }

    public String getBannerTitle() {

        return bannerTitle.getText();
    }

}
