package uk.gov.dvsa.motr.web.system.binder.factory;

import uk.gov.dvsa.motr.web.validator.MotDueDateValidator;

import java.time.LocalDate;

public class MotDueDateValidatorFactory implements BaseFactory<MotDueDateValidator> {

    @Override
    public MotDueDateValidator provide() {

        return new MotDueDateValidator();
    }
}
