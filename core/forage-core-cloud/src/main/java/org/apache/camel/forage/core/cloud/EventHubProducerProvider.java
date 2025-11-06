package org.apache.camel.forage.core.cloud;

import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;
import org.apache.camel.forage.core.common.BeanProvider;

public interface EventHubProducerProvider extends BeanProvider<EventHubProducerAsyncClient> {

    @Override
    default EventHubProducerAsyncClient create() {
        return create(null);
    }

    @Override
    EventHubProducerAsyncClient create(String id);
}
