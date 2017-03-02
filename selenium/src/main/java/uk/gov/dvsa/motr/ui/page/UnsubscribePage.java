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
    protected String getIdentity() {return "Are you sure you want to unsubscribe?";}

    public UnsubscribeConfirmationPage clickUnsubscribe() {

        unsubscribeButton.click();
        return new UnsubscribeConfirmationPage();
    }
}
