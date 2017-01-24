package uk.gov.dvsa.motr.base;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import uk.gov.dvsa.motr.logging.Logger;
import uk.gov.dvsa.motr.WebDriverConfigurator;
import uk.gov.dvsa.motr.WebDriverConfiguratorRegistry;
import uk.gov.dvsa.motr.config.Configurator;
import uk.gov.dvsa.motr.config.TestExecutionListener;
import uk.gov.dvsa.motr.config.webdriver.BaseAppDriver;

import java.text.SimpleDateFormat;
import java.util.Date;

@Listeners(TestExecutionListener.class)
public abstract class BaseTest {

    protected BaseAppDriver driver = null;
    private final static SimpleDateFormat screenshotDateFormat =
            new SimpleDateFormat("yyyyMMdd-HHmmss");

    @BeforeMethod(alwaysRun = true)
    public void setupBaseTest() {

        driver = WebDriverConfiguratorRegistry.get().getDriver();
        driver.setBaseUrl(Configurator.baseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.isSuccess()) {
            if (null != driver) {
                driver.manage().deleteAllCookies();
            }
        } else {
            WebDriverConfigurator cachedDriver = WebDriverConfiguratorRegistry.get();

            // Take screenshot on test failure
            if (cachedDriver != null && result.getStatus() == ITestResult.FAILURE && Configurator.isErrorScreenshotEnabled()) {
                driver.takeScreenShot(buildScreenShotPath(result));
            }

            if (null != cachedDriver) {
                Logger.error("Tearing down webdriver because of test failure");
                cachedDriver.destroy();
                WebDriverConfiguratorRegistry.clear();
            }
            driver = null;
        }
    }

    private static String buildScreenShotPath(ITestResult result) {

        String dir = Configurator.getErrorScreenshotPath() + "/" + Configurator.getBuildNumber();

        return String.format("%s/%s.%s_%s.png",
                dir,
                result.getTestClass().getName().replace("uk.gov.dvsa.motr", ""),
                result.getName(),
                screenshotDateFormat.format(new Date())
        );
    }
}
