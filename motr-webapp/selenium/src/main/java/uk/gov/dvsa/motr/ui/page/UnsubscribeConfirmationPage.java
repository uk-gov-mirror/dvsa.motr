package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;
import uk.gov.dvsa.motr.ui.base.PageIdentityVerificationException;

@GotoUrl("/unsubscribe/confirmed")
public class UnsubscribeConfirmationPage extends Page {

    @FindBy(id = "confirmation-heading")
    private WebElement bannerTitle;

    @FindBy(id = "unsubscribe-survey")
    private WebElement surveyLink;

    @Override
    protected void selfVerify() {

        if (!getBannerTitle().contains(getContentHeader()) || !this.driver.getTitle().equals(getPageTitle())) {

            throw new PageIdentityVerificationException("Page identity verification failed: " +
                    String.format("\n Expected: %s page, \n Found: %s page" +
                                    "\n with expected page title of: %s \n and actual page title of: %s",
                    getContentHeader(), getBannerTitle(), getPageTitle(), this.driver.getTitle())
            );
        }
    }

    @Override
    protected String getContentHeader() {

        return "You’ve unsubscribed";
    }

    @Override
    protected String getPageTitle() {

        return "You’ve unsubscribed - MOT reminders";
    }

    public String getBannerTitle() {

        return bannerTitle.getText();
    }

    public boolean isSurveyLinkDisplayed() {

        return surveyLink.isDisplayed();
    }
}
