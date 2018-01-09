package uk.gov.dvsa.motr;

public class WebDriverConfiguratorRegistry {

    private static final ThreadLocal<WebDriverConfigurator> webDriverConfigurator =
            new ThreadLocal<>();


    public static WebDriverConfigurator get() {

        WebDriverConfigurator configurator = webDriverConfigurator.get();
        if (configurator == null) {
            configurator = new WebDriverConfigurator();
            webDriverConfigurator.set(configurator);
        }
        return configurator;
    }

    public static void clear() {

        webDriverConfigurator.remove();
    }
}
