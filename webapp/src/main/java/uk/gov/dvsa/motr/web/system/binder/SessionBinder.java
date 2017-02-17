package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.system.binder.factory.BaseFactory;

import javax.inject.Singleton;

public class SessionBinder extends AbstractBinder {

    @Override
    protected void configure() {

        bindFactory(MotrSessionFactory.class).to(MotrSession.class).in(Singleton.class);
    }

    private static class MotrSessionFactory implements BaseFactory<MotrSession> {

        @Override
        public MotrSession provide() {

            return new MotrSession();
        }
    }
}
