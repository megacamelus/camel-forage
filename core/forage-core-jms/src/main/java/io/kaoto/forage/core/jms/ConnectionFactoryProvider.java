package io.kaoto.forage.core.jms;

import io.kaoto.forage.core.common.BeanProvider;
import jakarta.jms.ConnectionFactory;

public interface ConnectionFactoryProvider extends BeanProvider<ConnectionFactory> {

    @Override
    default ConnectionFactory create() {
        return create(null);
    }

    @Override
    ConnectionFactory create(String id);
}
