package uk.gov.dvsa.motr.smsreceiver.system.binder.factory;

import org.glassfish.hk2.api.Factory;

/**
 * Base interface for all factories in the application - they do not need to implement dispose
 */
public interface BaseFactory<T> extends Factory<T> {

    @Override
    default void dispose(T instance) {
    }
}
