# MistralAI Model Provider

This module provides integration with MistralAI for the Forage framework, enabling you to use MistralAI's powerful language models in your applications.

## Configuration

The MistralAI provider supports configuration through environment variables, system properties, and configuration files.

### Required Configuration

- **API Key**: Your MistralAI API key (required)

### Optional Configuration

- **Model Name**: The specific model to use (defaults to "mistral-large-latest")
- **Temperature**: Controls randomness (0.0-1.0)
- **Max Tokens**: Maximum tokens in response
- **Top-P**: Nucleus sampling parameter (0.0-1.0)
- **Random Seed**: Random seed for reproducible results
- **Timeout**: Request timeout in seconds
- **Max Retries**: Maximum retry attempts
- **Request/Response Logging**: Enable detailed logging

### Environment Variables

```bash
# Required Environment variables
export MISTRALAI_API_KEY="your-api-key"

# Optional Environment variables
export MISTRALAI_MODEL_NAME="mistral-large-latest"
export MISTRALAI_TEMPERATURE="0.7"
export MISTRALAI_MAX_TOKENS="2048"
export MISTRALAI_TOP_P="0.9"
export MISTRALAI_RANDOM_SEED="12345"
export MISTRALAI_TIMEOUT="60"
export MISTRALAI_MAX_RETRIES="3"
export MISTRALAI_LOG_REQUESTS_AND_RESPONSES="false"
```

### System Properties

```bash
# Required System properties
-Dmistralai.api.key=your-api-key

# Optional System properties
-Dmistralai.model.name=mistral-large-latest
-Dmistralai.temperature=0.7
-Dmistralai.max.tokens=2048
-Dmistralai.top.p=0.9
-Dmistralai.random.seed=12345
-Dmistralai.timeout=60
-Dmistralai.max.retries=3
-Dmistralai.log.requests.and.responses=false
```

### Configuration File

Create a `forage-model-mistral-ai.properties` file in your classpath:

```properties
# Required
mistralai.api.key=your-api-key

# Optional
mistralai.model.name=mistral-large-latest
mistralai.temperature=0.7
mistralai.max.tokens=2048
mistralai.top.p=0.9
mistralai.random.seed=12345
mistralai.timeout=60
mistralai.max.retries=3
mistralai.log.requests.and.responses=false
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
| `api-key` | String | *Required* | MistralAI API key |
| `model-name` | String | "mistral-large-latest" | Model name to use |
| `temperature` | Double | None | Controls randomness (0.0-1.0) |
| `max-tokens` | Integer | None | Maximum tokens in response |
| `top-p` | Double | None | Nucleus sampling parameter (0.0-1.0) |
| `random-seed` | Integer | None | Random seed for reproducible results |
| `timeout` | Integer | None | Request timeout in seconds |
| `max-retries` | Integer | None | Maximum retry attempts |
| `log-requests-and-responses` | Boolean | None | Enable request/response logging |

## Available Models

MistralAI offers several models with different capabilities:

- **mistral-large-latest**: Latest large model with highest capabilities
- **mistral-medium**: Medium-sized model balancing performance and cost
- **mistral-small**: Small, fast model for simpler tasks
- **mistral-tiny**: Smallest, fastest model for basic applications

## API Key Setup

1. Visit the [MistralAI Console](https://console.mistral.ai/)
2. Sign up or log in to your account
3. Navigate to the API Keys section
4. Create a new API key
5. Set the `MISTRALAI_API_KEY` environment variable or configure it via other methods

## Security Considerations

- Never commit API keys to version control
- Use environment variables or secure configuration management for production
- Monitor API usage and costs through the MistralAI console