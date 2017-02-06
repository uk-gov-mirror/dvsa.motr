package uk.gov.dvsa.motr.web.config.resolver;

import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;

import uk.gov.dvsa.motr.web.helper.ConfigValue;
import uk.gov.dvsa.motr.web.system.binder.factory.resolver.ConfigValueResolverFactory;

import javax.inject.Singleton;

@Singleton
public class ConfigValueResolver extends ParamInjectionResolver<ConfigValue> {

    public ConfigValueResolver() {

        super(ConfigValueResolverFactory.class);
    }
}
