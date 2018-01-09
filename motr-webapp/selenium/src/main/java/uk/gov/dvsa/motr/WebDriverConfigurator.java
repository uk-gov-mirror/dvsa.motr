package uk.gov.dvsa.motr;

import uk.gov.dvsa.motr.config.Configurator;
import uk.gov.dvsa.motr.config.webdriver.BaseAppDriver;
import uk.gov.dvsa.motr.config.webdriver.DriverFactory;

import java.util.concurrent.TimeUnit;

public class WebDriverConfigurator extends Configurator {

    private final BaseAppDriver cachedDriver;

    private final Thread hook;

    public WebDriverConfigurator() {

        DriverFactory driverFactory = new DriverFactory();

        cachedDriver = driverFactory.getDriver(this);

        cachedDriver.manage().timeouts()
                .implicitlyWait(getDefaultDriverTimeout(), TimeUnit.SECONDS);
        cachedDriver.manage().deleteAllCookies();

        hook = closeWebdriverOnShutdown();
    }

    private Thread closeWebdriverOnShutdown() {

        if (null != this.hook) {
            throw new IllegalStateException();
        }
        Thread hookForShutdown = new Thread(cachedDriver::quit);
        Runtime.getRuntime().addShutdownHook(hookForShutdown);
        return hookForShutdown;
    }

    public void destroy() {
        // Test failed, so nuke browser in case it's in a bad state.
        cachedDriver.quit();
        Runtime.getRuntime().removeShutdownHook(hook);
    }

    public BaseAppDriver getDriver() {

        return cachedDriver;
    }
}
