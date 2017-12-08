package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.web.cookie.MotrSession;

import javax.inject.Singleton;

public class SessionBinder extends AbstractBinder {

    @Override
    protected void configure() {

        bind(MotrSession.class).to(MotrSession.class).in(Singleton.class);
    }
}
