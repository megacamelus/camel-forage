# Azure Event Hubs Provider

This module provides an Azure Event Hubs producer client for Apache Forage.

## Overview

Azure Event Hubs is a fully managed, real-time data ingestion service that's simple, trusted, and scalable. This provider creates an `EventHubProducerAsyncClient` configured through properties-based configuration.

## Configuration

### Required Properties

| Property | Environment Variable | Description |
|----------|---------------------|-------------|
| `azure.eventhubs.eventhub.name` | `AZURE_EVENTHUBS_EVENTHUB_NAME` | The Event Hub name |
| `azure.eventhubs.fully.qualified.namespace` | `AZURE_EVENTHUBS_FULLY_QUALIFIED_NAMESPACE` | The fully qualified namespace (e.g., `<namespace>.servicebus.windows.net`) |

### Optional Properties

| Property | Environment Variable | Default | Description |
|----------|---------------------|---------|-------------|
| `azure.eventhubs.consumer.group` | `AZURE_EVENTHUBS_CONSUMER_GROUP` | `$Default` | The consumer group name |
| `azure.eventhubs.prefetch.count` | `AZURE_EVENTHUBS_PREFETCH_COUNT` | `100` | Number of events to prefetch |
| `azure.eventhubs.custom.endpoint.address` | `AZURE_EVENTHUBS_CUSTOM_ENDPOINT_ADDRESS` | - | Custom endpoint address for special Azure environments |

## Authentication

This provider uses **Azure DefaultAzureCredential** for authentication, which automatically attempts authentication through multiple methods in the following order:

1. Environment variables (`AZURE_CLIENT_ID`, `AZURE_TENANT_ID`, `AZURE_CLIENT_SECRET`)
2. Managed Identity (when running in Azure)
3. Azure CLI credentials
4. IntelliJ credentials
5. Visual Studio Code credentials

### Setting up Environment Variables for Authentication

```bash
export AZURE_CLIENT_ID="your-client-id"
export AZURE_TENANT_ID="your-tenant-id"
export AZURE_CLIENT_SECRET="your-client-secret"
```

## Configuration Examples

### Using Environment Variables

```bash
# Required
export AZURE_EVENTHUBS_EVENTHUB_NAME="my-eventhub"
export AZURE_EVENTHUBS_FULLY_QUALIFIED_NAMESPACE="my-namespace.servicebus.windows.net"

# Optional
export AZURE_EVENTHUBS_CONSUMER_GROUP="my-consumer-group"
export AZURE_EVENTHUBS_PREFETCH_COUNT="200"
```

### Using System Properties

```bash
java -Dazure.eventhubs.eventhub.name=my-eventhub \
     -Dazure.eventhubs.fully.qualified.namespace=my-namespace.servicebus.windows.net \
     -Dazure.eventhubs.consumer.group=my-consumer-group \
     -jar myapp.jar
```

### Using Configuration File

Create or modify `forage-azure-eventhubs.properties`:

```properties
azure.eventhubs.eventhub.name=my-eventhub
azure.eventhubs.fully.qualified.namespace=my-namespace.servicebus.windows.net
azure.eventhubs.consumer.group=my-consumer-group
azure.eventhubs.prefetch.count=200
```

## Named Configurations

You can create multiple named configurations for different Event Hubs:

### Environment Variables

```bash
# Default configuration
export AZURE_EVENTHUBS_EVENTHUB_NAME="default-hub"
export AZURE_EVENTHUBS_FULLY_QUALIFIED_NAMESPACE="default-ns.servicebus.windows.net"

# Named configuration for "analytics"
export analytics.azure.eventhubs.eventhub.name="analytics-hub"
export analytics.azure.eventhubs.fully.qualified.namespace="analytics-ns.servicebus.windows.net"

# Named configuration for "logging"
export logging.azure.eventhubs.eventhub.name="logging-hub"
export logging.azure.eventhubs.fully.qualified.namespace="logging-ns.servicebus.windows.net"
```

### System Properties

```bash
java -Dazure.eventhubs.eventhub.name=default-hub \
     -Danalytics.azure.eventhubs.eventhub.name=analytics-hub \
     -Dlogging.azure.eventhubs.eventhub.name=logging-hub \
     -jar myapp.jar
```

## Usage

### Programmatic Usage

```java
import io.kaoto.forage.core.cloud.EventHubProducerProvider;
import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;

// Default configuration
EventHubProducerProvider provider = new AzureEventHubsProvider();
EventHubProducerAsyncClient client = provider.create();

// Named configuration
EventHubProducerAsyncClient analyticsClient = provider.create("analytics");
EventHubProducerAsyncClient loggingClient = provider.create("logging");
```

### Using with Apache Camel

```java
// In your Camel route or configuration
EventHubProducerProvider provider = new AzureEventHubsProvider();
EventHubProducerAsyncClient client = provider.create();

// Register as a bean in your registry
bindToRegistry("eventHubClient", client);
```

## Dependencies

This module requires:

- `azure-messaging-eventhubs` (version 5.18.0 or later)
- `azure-identity` (for DefaultAzureCredential)

Maven:

```xml
<dependency>
    <groupId>io.kaoto.forage</groupId>
    <artifactId>forage-azure-eventhubs</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Azure Permissions Required

The Azure service principal or managed identity needs the following permissions:

- **Azure Event Hubs Data Sender** role (for sending events)
- **Azure Event Hubs Data Receiver** role (for receiving events)

## Troubleshooting

### Missing Configuration Error

If you see `MissingConfigException`, ensure required properties are set:

```
Missing Event Hub name is required but not configured
```

Set the required property:

```bash
export AZURE_EVENTHUBS_EVENTHUB_NAME="my-eventhub"
```

### Authentication Errors

If authentication fails:

1. Verify Azure credentials are properly configured
2. Check that the service principal has the correct permissions
3. Ensure the fully qualified namespace is correct
4. Verify network connectivity to Azure

### Connection Issues

If you cannot connect to Event Hubs:

1. Verify the fully qualified namespace format: `<namespace>.servicebus.windows.net`
2. Check firewall rules in Azure Event Hubs
3. Ensure the Event Hub exists in the specified namespace
4. Verify the consumer group exists (or use `$Default`)

## Additional Resources

- [Azure Event Hubs Documentation](https://docs.microsoft.com/azure/event-hubs/)
- [Azure SDK for Java - Event Hubs](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/eventhubs)
- [DefaultAzureCredential Documentation](https://docs.microsoft.com/java/api/com.azure.identity.defaultazurecredential)
