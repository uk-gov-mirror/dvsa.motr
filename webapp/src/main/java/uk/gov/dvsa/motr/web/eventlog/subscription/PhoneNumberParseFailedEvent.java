package uk.gov.dvsa.motr.web.eventlog.subscription;

import uk.gov.dvsa.motr.eventlog.Event;

public class PhoneNumberParseFailedEvent extends Event {

    public PhoneNumberParseFailedEvent setPhoneNumber(String phoneNumber) {

        params.put("phone-number", phoneNumber);
        return this;
    }

    @Override
    public String getCode() {

        return "PHONE-NUMBER-PARSE-FAILED";
    }
}
