package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/")
public class HomePage extends Page {

    @FindBy(className = "heading-xlarge")
    protected WebElement title;

    @FindBy(id = "startButton")
    private WebElement startNowButton;

    @Override
    protected String getContentHeader() {

        return "Get an annual MOT reminder";
    }

    @Override
    protected String getPageTitle() {

        return "Get an annual MOT reminder";
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

    @Override
    public final String getTitle() {

        return title.getText();
    }
}
