package uk.gov.dvsa.motr.web.helper;

import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.UriBuilder;

import static uk.gov.dvsa.motr.web.system.SystemVariable.BASE_URL;

public class UnsubscriptionUrlHelper {

    private String baseUrl;

    public UnsubscriptionUrlHelper(@SystemVariableParam(BASE_URL) String baseUrl) {

        this.baseUrl = baseUrl;
    }

    public String build(String subscriptionId) throws Exception {

        if (StringUtils.isEmpty(subscriptionId)) {
            throw new Exception("Subscription id must not be null or empty");
        }

        return UriBuilder.fromPath(this.baseUrl).path("unsubscribe").path(subscriptionId).build().toString();
    }
}
