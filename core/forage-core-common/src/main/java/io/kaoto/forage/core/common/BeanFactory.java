package io.kaoto.forage.core.common;

import java.util.List;
import java.util.ServiceLoader;
import org.apache.camel.CamelContextAware;

/**
 * Factory interface for creating and configuring beans within the Forage ecosystem.
 * <p>
 * Implementations of this interface are automatically discovered via ServiceLoader mechanism
 * and are called during Camel context initialization to configure beans.
 * </p>
 */
public interface BeanFactory extends CamelContextAware {

    /**
     * Configures and creates beans for the Camel context.
     * This method is called automatically during Camel context initialization.
     */
    void configure();

    /**
     * Utility method to find service providers of a specific type using ServiceLoader.
     *
     * @param <K> the type of service to find
     * @param type the class type to search for
     * @return a list of ServiceLoader providers for the specified type
     */
    default <K> List<ServiceLoader.Provider<K>> findProviders(Class<K> type) {
        ServiceLoader<K> modelLoader =
                ServiceLoader.load(type, getCamelContext().getApplicationContextClassLoader());

        return modelLoader.stream().toList();
    }
}
