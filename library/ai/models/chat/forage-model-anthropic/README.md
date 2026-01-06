# Anthropic Model Provider

This module provides integration with Anthropic's Claude models for the Forage framework.

⚠️ **Note:** This is currently a placeholder implementation. The provider will throw an `UnsupportedOperationException` until LangChain4j adds full Anthropic support.

## Configuration

The Anthropic provider supports configuration through environment variables, system properties, and configuration files.

### Required Configuration

- **API Key**: Your Anthropic API key for authentication

### Environment Variables

```bash
# Required Environment variables
export ANTHROPIC_API_KEY="sk-ant-api03-..."

# Optional Environment variables
export ANTHROPIC_MODEL_NAME="claude-3-sonnet-20240229"
export ANTHROPIC_TEMPERATURE="0.7"
export ANTHROPIC_MAX_TOKENS="2048"
export ANTHROPIC_TOP_P="0.9"
export ANTHROPIC_TOP_K="50"
export ANTHROPIC_STOP_SEQUENCES="Human:,Assistant:"
export ANTHROPIC_TIMEOUT="60"
export ANTHROPIC_MAX_RETRIES="3"
export ANTHROPIC_LOG_REQUESTS_AND_RESPONSES="false"
```

### System Properties

```bash
# Required System properties
-Danthropic.api.key=sk-ant-api03-...

# Optional System properties
-Danthropic.model.name=claude-3-sonnet-20240229
-Danthropic.temperature=0.7
-Danthropic.max.tokens=2048
-Danthropic.top.p=0.9
-Danthropic.top.k=50
-Danthropic.stop.sequences=Human:,Assistant:
-Danthropic.timeout=60
-Danthropic.max.retries=3
-Danthropic.log.requests.and.responses=false
```

### Configuration File

Create a `forage-model-anthropic.properties` file in your classpath:

```properties
# Required
anthropic.api.key=sk-ant-api03-...

# Optional
anthropic.model.name=claude-3-sonnet-20240229
anthropic.temperature=0.7
anthropic.max.tokens=2048
anthropic.top.p=0.9
anthropic.top.k=50
anthropic.stop.sequences=Human:,Assistant:
anthropic.timeout=60
anthropic.max.retries=3
anthropic.log.requests.and.responses=false
```

## Usage

⚠️ **Current Status**: This provider is not yet functional and will throw an `UnsupportedOperationException` when used. It serves as a placeholder for future Anthropic integration once LangChain4j adds full support.

Once functional, it will be automatically discovered via ServiceLoader:

```java
from("direct:chat")
    .to("langchain4j-agent:my-agent?agentFactory=#class:io.kaoto.forage.agent.factory.MultiAgentFactory")
    .log("Response: ${body}");
```

## Configuration Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `api-key` | String | *Required* | Anthropic API key |
| `model-name` | String | claude-3-sonnet-20240229 | Claude model to use |
| `temperature` | Double | 0.7 | Controls randomness (0.0-2.0) |
| `max-tokens` | Integer | 2048 | Maximum tokens in response |
| `top-p` | Double | 0.9 | Nucleus sampling parameter |
| `top-k` | Integer | 50 | Top-k sampling parameter |
| `stop-sequences` | String | None | Comma-separated stop sequences |
| `timeout` | Integer | 60 | Request timeout in seconds |
| `max-retries` | Integer | 3 | Maximum retry attempts |
| `log-requests-and-responses` | Boolean | false | Enable request/response logging |

## Supported Models

Once functional, this provider will support:

- **claude-3-opus-20240229** - Most capable Claude 3 model
- **claude-3-sonnet-20240229** - Balanced Claude 3 model
- **claude-3-haiku-20240307** - Fastest Claude 3 model
- **claude-instant-1.2** - Fast and efficient model

## Security Considerations

- Never commit API keys to version control
- Use environment variables or secure configuration management in production
- Be cautious with request/response logging as it may expose sensitive data

## Future Development

This module is prepared for Anthropic integration and will be activated once:
1. LangChain4j adds full Anthropic support
2. The provider implementation is updated to use the actual Anthropic client
3. Testing is completed