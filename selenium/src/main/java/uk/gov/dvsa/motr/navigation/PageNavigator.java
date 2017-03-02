package uk.gov.dvsa.motr.navigation;

import uk.gov.dvsa.motr.WebDriverConfiguratorRegistry;
import uk.gov.dvsa.motr.ui.base.Page;

import java.text.MessageFormat;
import java.util.stream.Stream;

public class PageNavigator {

    public static <T extends Page> T goTo(Class<T> pageClass, Object... params) {

        System.out.println(pageClass.getAnnotations().length);
        GotoUrl urlAn = ((GotoUrl) Stream.of(pageClass.getAnnotations())
                .filter(a -> a.annotationType().equals(GotoUrl.class))
                .findFirst()
                .orElseThrow(RuntimeException::new));

        WebDriverConfiguratorRegistry.get().getDriver().navigateToPath(MessageFormat.format(urlAn.value(), params));

        try {
            return pageClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
