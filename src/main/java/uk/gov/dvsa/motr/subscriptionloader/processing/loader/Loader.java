package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

import com.amazonaws.services.lambda.runtime.Context;

import java.time.LocalDate;

public interface Loader {

    LoadReport run(LocalDate today, Context context) throws Exception;
}
