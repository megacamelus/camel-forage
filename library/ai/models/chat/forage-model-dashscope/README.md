# Dashscope Model Provider

This module provides integration with Alibaba's Dashscope (Qwen) models for the Forage framework.

⚠️ **Note:** This is currently a placeholder implementation. The provider will throw an `UnsupportedOperationException` until LangChain4j adds Dashscope support.

## Configuration

The Dashscope provider supports configuration through environment variables, system properties, and configuration files.

### Required Configuration

- **API Key**: Your Dashscope API key for authentication

### Environment Variables

```bash
# Required Environment variables
export DASHSCOPE_API_KEY="sk-..."

# Optional Environment variables
export DASHSCOPE_MODEL_NAME="qwen-max"
export DASHSCOPE_TEMPERATURE="0.7"
export DASHSCOPE_MAX_TOKENS="2048"
export DASHSCOPE_TOP_P="0.9"
export DASHSCOPE_TOP_K="50"
export DASHSCOPE_REPETITION_PENALTY="1.1"
export DASHSCOPE_SEED="12345"
export DASHSCOPE_ENABLE_SEARCH="true"
export DASHSCOPE_TIMEOUT="60"
export DASHSCOPE_MAX_RETRIES="3"
export DASHSCOPE_LOG_REQUESTS_AND_RESPONSES="false"
```

### System Properties

```bash
# Required System properties
-Ddashscope.api.key=sk-...

# Optional System properties
-Ddashscope.model.name=qwen-max
-Ddashscope.temperature=0.7
-Ddashscope.max.tokens=2048
-Ddashscope.top.p=0.9
-Ddashscope.top.k=50
-Ddashscope.repetition.penalty=1.1
-Ddashscope.seed=12345
-Ddashscope.enable.search=true
-Ddashscope.timeout=60
-Ddashscope.max.retries=3
-Ddashscope.log.requests.and.responses=false
```

### Configuration File

Create a `forage-model-dashscope.properties` file in your classpath:

```properties
# Required
dashscope.api.key=sk-...

# Optional
dashscope.model.name=qwen-max
dashscope.temperature=0.7
dashscope.max.tokens=2048
dashscope.top.p=0.9
dashscope.top.k=50
dashscope.repetition.penalty=1.1
dashscope.seed=12345
dashscope.enable.search=true
dashscope.timeout=60
dashscope.max.retries=3
dashscope.log.requests.and.responses=false
```

## Usage

⚠️ **Current Status**: This provider is not yet functional and will throw an `UnsupportedOperationException` when used. It serves as a placeholder for future Dashscope integration once LangChain4j adds full support.

Once functional, it will be automatically discovered via ServiceLoader:

```java
from("direct:chat")
    .to("langchain4j-agent:my-agent?agentFactory=#class:io.kaoto.forage.agent.factory.MultiAgentFactory")
    .log("Response: ${body}");
```

## Configuration Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `api-key` | String | *Required* | Dashscope API key |
| `model-name` | String | qwen-max | Qwen model to use |
| `temperature` | Double | 0.7 | Controls randomness (0.0-2.0) |
| `max-tokens` | Integer | 2048 | Maximum tokens in response |
| `top-p` | Double | 0.9 | Nucleus sampling parameter |
| `top-k` | Integer | 50 | Top-k sampling parameter |
| `repetition-penalty` | Double | 1.1 | Penalty for repeating tokens |
| `seed` | Long | None | Seed for deterministic responses |
| `enable-search` | Boolean | false | Enable web search capabilities |
| `timeout` | Integer | 60 | Request timeout in seconds |
| `max-retries` | Integer | 3 | Maximum retry attempts |
| `log-requests-and-responses` | Boolean | false | Enable request/response logging |

## Supported Models

Once functional, this provider will support Alibaba's Qwen models:

- **qwen-max** - Most capable Qwen model
- **qwen-plus** - Balanced performance and efficiency
- **qwen-turbo** - Fast and cost-effective
- **qwen-7b-chat** - 7B parameter chat model
- **qwen-14b-chat** - 14B parameter chat model

## Security Considerations

- Never commit API keys to version control
- Use environment variables or secure configuration management in production
- Be cautious with request/response logging as it may expose sensitive data

## Future Development

This module is prepared for Dashscope integration and will be activated once:
1. LangChain4j adds full Dashscope support
2. The provider implementation is updated to use the actual Dashscope client
3. Testing is completed

## About Dashscope

Dashscope is Alibaba Cloud's machine learning platform providing access to:
- Large language models (Qwen series)
- Multimodal models
- Speech and vision capabilities
- Chinese language specialization