package uk.gov.dvsa.motr.web.helper;

import javax.ws.rs.core.UriBuilder;

import static uk.gov.dvsa.motr.web.system.SystemVariable.BASE_URL;

public class UnsubscriptionUrlHelper {

    private String baseUrl;

    public UnsubscriptionUrlHelper(@SystemVariableParam(BASE_URL) String baseUrl) {

        this.baseUrl = baseUrl;
    }

    public String build(String subscriptionId) {

        return UriBuilder.fromPath(this.baseUrl).path("unsubscribe").path(subscriptionId).build().toString();
    }
}
