package uk.gov.dvsa.motr.notifier.helpers;

import java.time.LocalDate;

public class EuRoadworthinessToggle {

    private String euGoLiveDateFromConfig;

    public EuRoadworthinessToggle(String euGoLiveDateFromConfig) {
        this.euGoLiveDateFromConfig = euGoLiveDateFromConfig;
    }

    public boolean isEuRoadworthinessLive() {

        LocalDate currentDate = getCurrentTime();
        LocalDate euGoLiveDate = LocalDate.parse(euGoLiveDateFromConfig);

        return currentDate.isAfter(euGoLiveDate) || currentDate.isEqual(euGoLiveDate);
    }

    /**
     * Wrapper method for testing purposes
     * @return LocalDate
     */
    public LocalDate getCurrentTime() {
        return LocalDate.now();
    }
}
