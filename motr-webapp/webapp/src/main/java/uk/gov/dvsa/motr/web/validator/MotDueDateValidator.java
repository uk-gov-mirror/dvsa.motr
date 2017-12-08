package uk.gov.dvsa.motr.web.validator;

import java.time.LocalDate;

public class MotDueDateValidator {
    private LocalDate now;

    public MotDueDateValidator(LocalDate now) {

        this.now = now;
    }

    public boolean isDueDateValid(LocalDate motDueDate) {

        return (motDueDate != null) && (motDueDate.isAfter(now) || motDueDate.isEqual(now));
    }
}
