package uk.gov.dvsa.motr.web.eventlog;

import uk.gov.dvsa.motr.eventlog.Event;

public class AccessEvent extends Event {

    @Override
    public String getCode() {
        return "ACCESS";
    }

    public AccessEvent setRequestMethod(String method) {

        params.put("request-method", method);
        return this;
    }

    public AccessEvent setRequestPath(String path) {

        params.put("request-path", path);
        return this;
    }

    public AccessEvent setQueryString(String val) {

        params.put("request-query-string", val);
        return this;
    }

    public AccessEvent setStatusCode(int val) {

        params.put("response-status-code", String.valueOf(val));
        return this;
    }

    public AccessEvent setResponseBodyLength(int val) {

        params.put("response-body-length", String.valueOf(val));
        return this;
    }

    public AccessEvent setColdStart(boolean isColdStart) {

        params.put("cold-start", String.valueOf(isColdStart));
        return this;
    }

    public AccessEvent setIpChain(String ipChain) {

        params.put("ip-chain", ipChain);
        return this;
    }
}
