package uk.gov.dvsa.motr.web.formatting;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PhoneNumberFormatter {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PhoneNumberFormatter.class);

    private static final String GB_REGION_CODE = "GB";

    private static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public static String normalizeUkPhoneNumber(String phoneNumber) {

        PhoneNumber ukNumberProto;

        try {
            ukNumberProto = phoneNumberUtil.parse(phoneNumber, GB_REGION_CODE);

            if (ukNumberProto != null) {
                return trimWhitespace(phoneNumberUtil.format(ukNumberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
            }
        } catch (NumberParseException exception) {
            logger.debug("There was a problem when trying to parse the phone number entered: " + exception.toString());
        }

        return null;
    }

    public static String trimWhitespace(String phoneNumber) {

        return phoneNumber.replaceAll("\\s+","").replaceAll("\\p{Cf}", "");
    }
}
