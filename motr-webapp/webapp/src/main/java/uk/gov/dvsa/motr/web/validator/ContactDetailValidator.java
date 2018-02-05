package uk.gov.dvsa.motr.web.validator;

import uk.gov.dvsa.motr.web.component.subscription.model.ContactDetail;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import javax.inject.Inject;

public class ContactDetailValidator {

    private final PhoneNumberValidator phoneNumberValidator;

    @Inject
    public ContactDetailValidator(PhoneNumberValidator phoneNumberValidator) {

        this.phoneNumberValidator = phoneNumberValidator;
    }

    public boolean isValid(ContactDetail contactDetail) {

        if (contactDetail == null) {
            return false;
        }

        if (contactDetail.getContactType() == Subscription.ContactType.EMAIL) {
            EmailValidator validator = new EmailValidator();
            return validator.isValid(contactDetail.getValue());
        }

        if (contactDetail.getContactType() == Subscription.ContactType.MOBILE) {
            return phoneNumberValidator.isValid(contactDetail.getValue());
        }

        return false;
    }
}
