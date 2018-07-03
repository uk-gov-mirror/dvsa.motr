package uk.gov.dvsa.motr.conversion;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

public class DataAnonymizer {

    public String anonymizeContactData(String contactData) {
        return Hashing.sha512().hashString(contactData, Charsets.UTF_8).toString();
    }
}
