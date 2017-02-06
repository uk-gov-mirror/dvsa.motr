package uk.gov.dvsa.motr.web.system.binder.factory.resolver;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.model.Parameter;

import uk.gov.dvsa.motr.web.config.Config;
import uk.gov.dvsa.motr.web.helper.ConfigValue;
import uk.gov.dvsa.motr.web.system.SystemVariable;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.glassfish.jersey.server.model.Parameter.Source.UNKNOWN;

import static uk.gov.dvsa.motr.web.system.SystemVariable.BASE_URL;

@Singleton
public class ConfigValueResolverFactory extends AbstractValueFactoryProvider {

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

        if (parameter.getAnnotation(ConfigValue.class) == null) {
            return null;
        }

        return new AbstractContainerRequestValueFactory<String>() {

            @Override
            public String provide() {

                SystemVariable systemVariable = null;

                if (parameter.getSourceName().equals("BASE_URL")) {
                    systemVariable = BASE_URL;
                }

                return getLocator().getService(Config.class).getValue(systemVariable);
            }
        };
    }
}
