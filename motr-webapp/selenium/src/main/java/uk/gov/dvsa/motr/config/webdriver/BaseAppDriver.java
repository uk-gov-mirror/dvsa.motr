package uk.gov.dvsa.motr.config.webdriver;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.RemoteWebDriver;

import uk.gov.dvsa.motr.logging.Logger;

import java.io.File;

public abstract class BaseAppDriver implements AppDriver {

    protected RemoteWebDriver remoteWebDriver;
    private String baseUrl = "";

    public BaseAppDriver(RemoteWebDriver remoteWebDriver) {

        this.remoteWebDriver = remoteWebDriver;
    }

    public void setBaseUrl(String baseUrl) {

        this.baseUrl = baseUrl;
    }

    public void loadBaseUrl() {

        remoteWebDriver.get(baseUrl);
    }

    public void navigateToPath(String path) {

        remoteWebDriver.get((baseUrl + path).replaceAll("(?<!(http:|https:))//", "/"));
    }

    public String getPageSource() {

        return this.remoteWebDriver.getPageSource();
    }

    public void takeScreenShot(String destinationPath) {

        try {
            File scrFile = remoteWebDriver.getScreenshotAs(OutputType.FILE);
            File screenshotFile = new File(destinationPath);

            if (!screenshotFile.exists()) {
                FileUtils.copyFile(scrFile, screenshotFile);
                Logger.info("PageUrl: " + remoteWebDriver.getCurrentUrl());
                Logger.info("Screenshot saved to: " + screenshotFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Logger.error("Error trying to take screen shot: " + e.getMessage(), e);
        }
    }

}
