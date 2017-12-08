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
    protected String getContentHeader() {

        return "Get MOT reminders";
    }

    @Override
    protected String getPageTitle() {

        return "Get MOT reminders";
    }

    public VrmPage clickStartNow() {

        startNowButton.click();
        return new VrmPage();
    }

    public CookiesPage clickCookiesLink() {

        cookiesLink.click();
        return new CookiesPage();
    }

    public TermsAndConditionsPage clickTermsAndConditionsLink(){

        termsAndConditionsLink.click();
        return new TermsAndConditionsPage();
    }

    public PrivacyPage clickPrivacyPolicyLink(){

        privacyPolicyLink.click();
        return new PrivacyPage();
    }
}
