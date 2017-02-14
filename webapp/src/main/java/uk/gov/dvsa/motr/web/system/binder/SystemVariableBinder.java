package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import uk.gov.dvsa.motr.web.config.Config;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.glassfish.jersey.server.model.Parameter.Source.UNKNOWN;

public class SystemVariableBinder extends AbstractBinder {

    @Override
    protected void configure() {

        bind(ConfigValueResolverFactory.class).to(ValueFactoryProvider.class).in(Singleton.class);
        bind(ConfigValueResolver.class).to(new TypeLiteral<InjectionResolver<SystemVariableParam>>() {}).in(Singleton.class);
    }

    @Singleton
    private static class ConfigValueResolver extends ParamInjectionResolver<SystemVariableParam> {

        @Inject
        public ConfigValueResolver() {

            super(ConfigValueResolverFactory.class);
        }
    }

    @Singleton
    private static class ConfigValueResolverFactory extends AbstractValueFactoryProvider {

        @Inject
        public ConfigValueResolverFactory(final MultivaluedParameterExtractorProvider extractorProvider, final ServiceLocator
                injector) {

            super(extractorProvider, injector, UNKNOWN);
        }

        @Override
        protected Factory<?> createValueFactory(final Parameter parameter) {

            final Class<?> classType = parameter.getRawType();

            if (classType == null || (!classType.equals(String.class))) {
                return null;
            }

            if (parameter.getAnnotation(SystemVariableParam.class) == null) {
                return null;
            }

            return new AbstractContainerRequestValueFactory<String>() {

                @Override
                public String provide() {

                    return getLocator().getService(Config.class).getValue(parameter.getAnnotation(SystemVariableParam.class).value());
                }
            };
        }
    }
}
