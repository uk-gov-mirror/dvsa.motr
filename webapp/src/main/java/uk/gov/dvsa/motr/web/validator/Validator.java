package uk.gov.dvsa.motr.web.validator;

/**
 * validator interface
 */
public interface Validator {

    /**
     * Returns true if the input matches the conditions set in the validator.  Else false
     *
     * @return is the input valid or not
     */
    boolean isValid();

    /**
     * Gets the message set by the validator
     *
     * @return the message set by the validator
     */
    String getMessage();
}
