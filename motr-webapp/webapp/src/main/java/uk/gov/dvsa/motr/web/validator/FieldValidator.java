package uk.gov.dvsa.motr.web.validator;

public interface FieldValidator extends Validator {

    String getMessage();

    void setMessage(String message);

    String getMessageAtField();

    void setMessageAtField(String messageAtField);
}
