# Azure OpenAI Model Provider

This module provides integration with Azure OpenAI services for the Forage framework, enabling you to use GPT models deployed in your Azure OpenAI resource.

## Configuration

The Azure OpenAI provider supports configuration through environment variables, system properties, and configuration files.

### Required Configuration

- **API Key**: Your Azure OpenAI API key for authentication
- **Endpoint**: The Azure OpenAI resource endpoint URL  
- **Deployment Name**: The deployment name of your Azure OpenAI model

### Environment Variables

```bash
# Required Environment variables
export AZURE_OPENAI_API_KEY="your-api-key"
export AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"
export AZURE_OPENAI_DEPLOYMENT_NAME="gpt-35-turbo"

# Optional Environment variables
export AZURE_OPENAI_SERVICE_VERSION="2024-02-01"
export AZURE_OPENAI_TEMPERATURE="0.7"
export AZURE_OPENAI_MAX_TOKENS="2048"
export AZURE_OPENAI_TOP_P="0.9"
export AZURE_OPENAI_PRESENCE_PENALTY="0.0"
export AZURE_OPENAI_FREQUENCY_PENALTY="0.0"
export AZURE_OPENAI_SEED="12345"
export AZURE_OPENAI_USER="user-123"
export AZURE_OPENAI_TIMEOUT="60"
export AZURE_OPENAI_MAX_RETRIES="3"
export AZURE_OPENAI_LOG_REQUESTS_AND_RESPONSES="false"
```

### System Properties

```bash
# Required System properties
-Dazure.openai.api.key=your-api-key
-Dazure.openai.endpoint=https://your-resource.openai.azure.com/
-Dazure.openai.deployment.name=gpt-35-turbo

# Optional System properties
-Dazure.openai.service.version=2024-02-01
-Dazure.openai.temperature=0.7
-Dazure.openai.max.tokens=2048
-Dazure.openai.top.p=0.9
-Dazure.openai.presence.penalty=0.0
-Dazure.openai.frequency.penalty=0.0
-Dazure.openai.seed=12345
-Dazure.openai.user=user-123
-Dazure.openai.timeout=60
-Dazure.openai.max.retries=3
-Dazure.openai.log.requests.and.responses=false
```

### Configuration File

Create a `forage-model-azure-openai.properties` file in your classpath:

```properties
# Required
azure.openai.api.key=your-api-key
azure.openai.endpoint=https://your-resource.openai.azure.com/
azure.openai.deployment.name=gpt-35-turbo

# Optional
azure.openai.service.version=2024-02-01
azure.openai.temperature=0.7
azure.openai.max.tokens=2048
azure.openai.top.p=0.9
azure.openai.presence.penalty=0.0
azure.openai.frequency.penalty=0.0
azure.openai.seed=12345
azure.openai.user=user-123
azure.openai.timeout=60
azure.openai.max.retries=3
azure.openai.log.requests.and.responses=false
```

## Usage

The provider is automatically discovered via ServiceLoader. Use it in your Camel routes like this:

```java
from("direct:chat")
    .to("langchain4j-agent:my-agent?agentFactory=#class:io.kaoto.forage.agent.factory.MultiAgentFactory")
    .log("Response: ${body}");
```

## Configuration Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `api-key` | String | *Required* | Azure OpenAI API key |
| `endpoint` | String | *Required* | Azure OpenAI resource endpoint |
| `deployment-name` | String | *Required* | Model deployment name |
| `service-version` | String | Latest | API version to use |
| `temperature` | Double | 1.0 | Controls randomness (0.0-2.0) |
| `max-tokens` | Integer | Model default | Maximum tokens in response |
| `top-p` | Double | 1.0 | Nucleus sampling parameter |
| `presence-penalty` | Double | 0.0 | Penalty for new topics (-2.0 to 2.0) |
| `frequency-penalty` | Double | 0.0 | Penalty for repetition (-2.0 to 2.0) |
| `seed` | Long | None | Seed for deterministic responses |
| `user` | String | None | User identifier for tracking |
| `timeout` | Integer | 60 | Request timeout in seconds |
| `max-retries` | Integer | 3 | Maximum retry attempts |
| `log-requests-and-responses` | Boolean | false | Enable request/response logging |

## Security Considerations

- Never commit API keys to version control
- Use environment variables or secure configuration management in production
- Be cautious with request/response logging as it may expose sensitive data

## Common Model Deployments

- **gpt-35-turbo** - GPT-3.5 Turbo
- **gpt-4** - GPT-4  
- **gpt-4-32k** - GPT-4 with 32k context window
- **text-embedding-ada-002** - Text embedding model