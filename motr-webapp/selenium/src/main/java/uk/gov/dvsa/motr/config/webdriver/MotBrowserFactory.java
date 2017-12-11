package uk.gov.dvsa.motr.config.webdriver;

import org.openqa.selenium.remote.RemoteWebDriver;

public class MotBrowserFactory {

    public static BaseAppDriver createMotDriver(RemoteWebDriver remoteWebDriver) {

        return new RemoteAppWebDriver(remoteWebDriver);
    }
}
