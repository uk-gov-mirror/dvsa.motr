package uk.gov.dvsa.motr.web.component.subscription.helper;

import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import static uk.gov.dvsa.motr.web.system.SystemVariable.BASE_URL;

public class EmailConfirmationUrlHelper {

    private String baseUrl;

    @Inject
    public EmailConfirmationUrlHelper(@SystemVariableParam(BASE_URL) String baseUrl) {

        this.baseUrl = baseUrl;
    }

    public String build(PendingSubscription subscription) {

        return UriBuilder.fromPath(this.baseUrl).path("confirm-email")
                .path(subscription.getConfirmationId()).build().toString();
    }
}
