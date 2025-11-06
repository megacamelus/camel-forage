package org.apache.camel.forage.cloud.eventhubs;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.apache.camel.forage.core.cloud.EventHubProducerProvider;

@ForageBean(
        value = "azure-eventhubs",
        components = {"camel-azure-eventhubs"},
        description = "Azure Event Hubs message streaming platform")
public class AzureEventHubsProvider implements EventHubProducerProvider {

    @Override
    public EventHubProducerAsyncClient create(String id) {
        final AzureEventHubsConfig config = new AzureEventHubsConfig(id);

        TokenCredential credential = new DefaultAzureCredentialBuilder().build();

        EventHubClientBuilder builder = new EventHubClientBuilder()
                .credential(config.fullyQualifiedNamespace(), config.eventHubName(), credential)
                .consumerGroup(config.consumerGroup());

        if (config.prefetchCount() > 0) {
            builder.prefetchCount(config.prefetchCount());
        }

        if (config.customEndpointAddress() != null) {
            builder.customEndpointAddress(config.customEndpointAddress());
        }

        return builder.buildAsyncProducerClient();
    }
}
