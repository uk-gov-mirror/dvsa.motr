package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

import java.time.LocalDate;

public interface Loader {

    void run(LocalDate today) throws Exception;
}
