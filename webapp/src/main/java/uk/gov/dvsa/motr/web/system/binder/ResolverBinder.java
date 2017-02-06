package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import uk.gov.dvsa.motr.web.config.resolver.ConfigValueResolver;
import uk.gov.dvsa.motr.web.helper.ConfigValue;
import uk.gov.dvsa.motr.web.system.binder.factory.resolver.ConfigValueResolverFactory;

import javax.inject.Singleton;

public class ResolverBinder extends AbstractBinder {

    @Override
    protected void configure() {

        bind(ConfigValueResolverFactory.class).to(ValueFactoryProvider.class).in(Singleton.class);
        bind(ConfigValueResolver.class).to(new TypeLiteral<InjectionResolver<ConfigValue>>() {}).in(Singleton.class);
    }
}
