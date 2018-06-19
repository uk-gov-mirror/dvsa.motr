package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/unknown-test-due-date")
public class UnknownTestDueDatePage extends Page {

    @FindBy(id = "contact-dvsa-link")
    private WebElement contactDvsaLink;

    @Override
    protected String getContentHeader() {

        return "We don't know when this vehicle's first annual test is due";
    }

    @Override
    protected String getPageTitle() {

        return "We don't know when this vehicle's first annual test is due - MOT reminders";
    }

    public WebElement getContactDvsaLink() {
        return contactDvsaLink;
    }
}
