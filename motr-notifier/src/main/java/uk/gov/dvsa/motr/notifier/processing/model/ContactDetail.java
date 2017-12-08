package uk.gov.dvsa.motr.notifier.processing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactDetail {

    @JsonProperty("email")
    private String value;

    @JsonProperty("contactType")
    private SubscriptionQueueItem.ContactType contactType;

    @JsonCreator
    public ContactDetail(@JsonProperty("email") String value, @JsonProperty("contactType") SubscriptionQueueItem.ContactType contactType) {

        this.value = value.toLowerCase();
        this.contactType = contactType;
    }

    public String getValue() {

        return value;
    }

    public SubscriptionQueueItem.ContactType getContactType() {

        return contactType;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContactDetail that = (ContactDetail) o;

        if (!value.equals(that.value)) {
            return false;
        }
        return contactType == that.contactType;
    }

    @Override
    public int hashCode() {

        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (contactType != null ? contactType.hashCode() : 0);
        return result;
    }
}
