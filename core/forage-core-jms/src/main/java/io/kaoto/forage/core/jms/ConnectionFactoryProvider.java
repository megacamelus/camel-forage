package io.kaoto.forage.core.jms;

import jakarta.jms.ConnectionFactory;
import io.kaoto.forage.core.common.BeanProvider;

public interface ConnectionFactoryProvider extends BeanProvider<ConnectionFactory> {

    @Override
    default ConnectionFactory create() {
        return create(null);
    }

    @Override
    ConnectionFactory create(String id);
}
