package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;
import uk.gov.dvsa.motr.ui.base.PageIdentityVerificationException;

@GotoUrl("/unsubscribe/confirmed")
public class UnsubscribeConfirmationPage extends Page {

    @FindBy(className = "banner__heading-large")
    private WebElement bannerTitle;

    @Override
    protected void selfVerify() {

        if (!getBannerTitle().contains(getContentHeader()) || !getPageTitle().equals(getPageTitle())) {

            throw new PageIdentityVerificationException("Page identity verification failed: "
                    + String.format("\n Expected: %s page, \n Found: %s page", getContentHeader(), getBannerTitle())
            );
        }
    }

    @Override
    protected String getContentHeader() {

        return "You’ve unsubscribed";
    }

    @Override
    protected String getPageTitle() {

        return "Are you sure you want to unsubscribe? – MOT reminders";
    }

    public String getBannerTitle() {

        return bannerTitle.getText();
    }
}
