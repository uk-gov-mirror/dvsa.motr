package uk.gov.dvsa.motr.web.validator;

import java.time.LocalDate;

public class MotDueDateValidator {

    public boolean isDueDateValid(LocalDate motDueDate) {

        LocalDate now = getNow();
        return (motDueDate != null) && (motDueDate.isAfter(now) || motDueDate.isEqual(now));
    }

    LocalDate getNow() {
        return LocalDate.now();
    }
}
