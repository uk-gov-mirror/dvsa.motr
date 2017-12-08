package uk.gov.dvsa.motr.ui.page;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/confirm-subscription/{0}")
public class SubscriptionConfirmationErrorPage extends Page {

    @Override
    protected String getContentHeader() {

        return "No MOT reminder found";
    }

    @Override
    protected String getPageTitle() {

        return "No MOT reminder found – MOT reminders";
    }
}
