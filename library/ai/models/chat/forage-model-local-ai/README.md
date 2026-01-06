# LocalAI Model Provider

This module provides integration with LocalAI for the Forage framework, enabling you to use self-hosted, OpenAI-compatible language models with complete privacy and control.

## Configuration

The LocalAI provider supports configuration through environment variables, system properties, and configuration files.

### Required Configuration

- **Base URL**: The LocalAI server endpoint URL

### Optional Configuration

- **API Key**: API key if your LocalAI server requires authentication (many setups don't)
- **Model Name**: The specific model to use (must be available on your LocalAI server)

### Environment Variables

```bash
# Required Environment variables
export LOCALAI_BASE_URL="http://localhost:8080"

# Optional Environment variables
export LOCALAI_API_KEY="your-api-key-if-needed"
export LOCALAI_MODEL_NAME="gpt-3.5-turbo"
export LOCALAI_TEMPERATURE="0.7"
export LOCALAI_MAX_TOKENS="2048"
export LOCALAI_TOP_P="0.9"
export LOCALAI_PRESENCE_PENALTY="0.0"
export LOCALAI_FREQUENCY_PENALTY="0.0"
export LOCALAI_SEED="12345"
export LOCALAI_USER="user-123"
export LOCALAI_TIMEOUT="60"
export LOCALAI_MAX_RETRIES="3"
export LOCALAI_LOG_REQUESTS_AND_RESPONSES="false"
```

### System Properties

```bash
# Required System properties
-Dlocalai.base.url=http://localhost:8080

# Optional System properties
-Dlocalai.api.key=your-api-key-if-needed
-Dlocalai.model.name=gpt-3.5-turbo
-Dlocalai.temperature=0.7
-Dlocalai.max.tokens=2048
-Dlocalai.top.p=0.9
-Dlocalai.presence.penalty=0.0
-Dlocalai.frequency.penalty=0.0
-Dlocalai.seed=12345
-Dlocalai.user=user-123
-Dlocalai.timeout=60
-Dlocalai.max.retries=3
-Dlocalai.log.requests.and.responses=false
```

### Configuration File

Create a `forage-model-local-ai.properties` file in your classpath:

```properties
# Required
localai.base.url=http://localhost:8080

# Optional
localai.api.key=your-api-key-if-needed
localai.model.name=gpt-3.5-turbo
localai.temperature=0.7
localai.max.tokens=2048
localai.top.p=0.9
localai.presence.penalty=0.0
localai.frequency.penalty=0.0
localai.seed=12345
localai.user=user-123
localai.timeout=60
localai.max.retries=3
localai.log.requests.and.responses=false
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
| `base-url` | String | *Required* | LocalAI server endpoint URL |
| `api-key` | String | "not-needed" | API key (optional for most LocalAI setups) |
| `model-name` | String | None | Model name (must be available on server) |
| `temperature` | Double | None | Controls randomness (0.0-2.0) |
| `max-tokens` | Integer | None | Maximum tokens in response |
| `top-p` | Double | None | Nucleus sampling parameter |
| `presence-penalty` | Double | None | Penalty for new topics (-2.0 to 2.0) |
| `frequency-penalty` | Double | None | Penalty for repetition (-2.0 to 2.0) |
| `seed` | Long | None | Seed for deterministic responses |
| `user` | String | None | User identifier for tracking |
| `timeout` | Integer | None | Request timeout in seconds |
| `max-retries` | Integer | None | Maximum retry attempts |
| `log-requests-and-responses` | Boolean | None | Enable request/response logging |