package uk.gov.dvsa.motr.web.component.subscription.model;

public class ContactDetail {

    private String value;

    private Subscription.ContactType contactType;

    public ContactDetail(String value, Subscription.ContactType contactType) {

        this.value = value.toLowerCase();
        this.contactType = contactType;
    }

    public String getValue() {

        return value;
    }

    public Subscription.ContactType getContactType() {

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
        int result = value.hashCode();
        result = 31 * result + contactType.hashCode();
        return result;
    }
}
