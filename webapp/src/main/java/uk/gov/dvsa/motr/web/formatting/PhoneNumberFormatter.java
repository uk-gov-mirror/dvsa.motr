package uk.gov.dvsa.motr.web.formatting;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.eventlog.subscription.PhoneNumberParseFailedEvent;

public class PhoneNumberFormatter {

    private static final String GB_REGION_CODE = "GB";

    private PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public String normalizeUkPhoneNumber(String phoneNumber) {

        PhoneNumber ukNumberProto = null;

        try {
            ukNumberProto = phoneNumberUtil.parse(phoneNumber, GB_REGION_CODE);
        } catch (NumberParseException exception) {
            EventLogger.logErrorEvent(new PhoneNumberParseFailedEvent().setPhoneNumber(phoneNumber), exception);
        }

        return trimWhitespace(phoneNumberUtil.format(ukNumberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
    }

    private String trimWhitespace(String phoneNumber) {

        return phoneNumber.replaceAll("\\s+","");
    }
}
