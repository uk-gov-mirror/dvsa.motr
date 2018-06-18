package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/vrm")
public class TestExpiredPage extends Page {

    @FindBy(id = "publications-link")
    private WebElement publicationsLink;

    @FindBy(id = "reminders-link")
    private WebElement remindersLink;

    @Override
    protected String getContentHeader() {

        return "This vehicle’s annual test expired on 09 January 2016";
    }

    @Override
    protected String getPageTitle() {

        return "MOT reminders";
    }

    public WebElement getPublicationsLink() {

        return publicationsLink;
    }

    public WebElement getRemindersLink() {

        return remindersLink;
    }
}
