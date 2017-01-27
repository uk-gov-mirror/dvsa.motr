package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.web.config.Config;
import uk.gov.dvsa.motr.web.render.HandlebarsTemplateEngine;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.system.binder.factory.BaseFactory;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.STATIC_ASSETS_HASH;
import static uk.gov.dvsa.motr.web.system.SystemVariable.STATIC_ASSETS_URL;

public class TemplateEngineBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bindFactory(TemplateEngineFactory.class).to(TemplateEngine.class);
    }

    private static class TemplateEngineFactory implements BaseFactory<TemplateEngine> {

        private final Config config;

        @Inject
        public TemplateEngineFactory(Config config) {
            this.config = config;
        }

        @Override
        public TemplateEngine provide() {

            String hash = config.getValue(STATIC_ASSETS_HASH);
            String rootPath = config.getValue(STATIC_ASSETS_URL);

            return new HandlebarsTemplateEngine(rootPath, hash);
        }
    }
}
