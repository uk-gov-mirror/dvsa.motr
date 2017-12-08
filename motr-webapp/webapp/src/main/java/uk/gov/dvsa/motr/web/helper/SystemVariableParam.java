package uk.gov.dvsa.motr.web.helper;

import uk.gov.dvsa.motr.web.system.SystemVariable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface SystemVariableParam {

    SystemVariable value();
}
