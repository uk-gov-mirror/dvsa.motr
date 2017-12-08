package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/unsubscribe/{0}")
public class UnsubscribePage extends Page {

    @FindBy(id = "cancel-button")
    private WebElement unsubscribeButton;


    @Override
    protected String getContentHeader() {

        return "Are you sure you want to unsubscribe?";
    }

    @Override
    protected String getPageTitle() {

        return "Are you sure you want to unsubscribe? – MOT reminders";
    }

    public UnsubscribeConfirmationPage clickUnsubscribe() {

        unsubscribeButton.click();
        return new UnsubscribeConfirmationPage();
    }
}
