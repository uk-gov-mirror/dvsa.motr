package uk.gov.dvsa.motr.web.component.subscription.helper;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import javax.ws.rs.core.UriBuilder;

import static uk.gov.dvsa.motr.web.system.SystemVariable.BASE_URL;

public class UnsubscriptionUrlHelper {

    private String baseUrl;

    public UnsubscriptionUrlHelper(@SystemVariableParam(BASE_URL) String baseUrl) {

        this.baseUrl = baseUrl;
    }

    public String build(Subscription subscription) {

        return UriBuilder.fromPath(this.baseUrl).path("unsubscribe")
                .path(subscription.getUnsubscribeId()).build().toString();
    }
}
