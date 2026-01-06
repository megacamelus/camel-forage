# Forage OpenAI Model

This module provides integration with OpenAI's GPT models for the Forage framework.

## Overview

The OpenAI model integration allows you to use OpenAI's powerful language models with Forage. This includes GPT-3.5, GPT-4, and other OpenAI models, providing state-of-the-art natural language processing capabilities.

## Dependencies

Add this dependency to your Maven project:

```xml
<dependency>
    <groupId>io.kaoto.forage</groupId>
    <artifactId>forage-model-open-ai</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Configuration

The OpenAI provider is configured through environment variables, system properties, or configuration files.

### Required Configuration

| Environment Variable | System Property | Description | Default |
|---------------------|-----------------|-------------|---------|
| `OPENAI_API_KEY` | `openai.api.key` | Your OpenAI API key | **Required** |

### Optional Configuration

| Environment Variable | System Property | Description | Default | Valid Range |
|---------------------|-----------------|-------------|---------|-------------|
| `OPENAI_MODEL_NAME` | `openai.model.name` | OpenAI model to use | `gpt-3.5-turbo` | See supported models |
| `OPENAI_BASE_URL` | `openai.base.url` | Custom API base URL | OpenAI default | Valid URL |
| `OPENAI_TEMPERATURE` | `openai.temperature` | Response randomness | None | 0.0 - 2.0 |
| `OPENAI_MAX_TOKENS` | `openai.max.tokens` | Maximum tokens to generate | None | Positive integers |
| `OPENAI_TOP_P` | `openai.top.p` | Nucleus sampling parameter | None | 0.0 - 1.0 |
| `OPENAI_FREQUENCY_PENALTY` | `openai.frequency.penalty` | Frequency penalty | None | -2.0 - 2.0 |
| `OPENAI_PRESENCE_PENALTY` | `openai.presence.penalty` | Presence penalty | None | -2.0 - 2.0 |
| `OPENAI_LOG_REQUESTS` | `openai.log.requests` | Enable request logging | None | true/false |
| `OPENAI_LOG_RESPONSES` | `openai.log.responses` | Enable response logging | None | true/false |

### Configuration Sources Priority

Configuration values are resolved in the following order of precedence:

1. Environment variables
2. System properties
3. `camel-forage-model-open-ai.properties` file in classpath
4. Default values (only for model name)

## Getting Started

### Prerequisites

1. **OpenAI Account**: Create an account at [OpenAI](https://platform.openai.com/)
2. **API Key**: Generate an API key from the OpenAI platform
3. **Credits/Billing**: Ensure you have sufficient credits or billing set up

### API Key Setup

#### Option 1: Environment Variable (Recommended)
```bash
export OPENAI_API_KEY="sk-..."  # Your actual API key
export OPENAI_MODEL_NAME="gpt-4"  # Optional, defaults to gpt-3.5-turbo
```

#### Option 2: System Properties
```bash
java -Dopenai.api.key="sk-..." -Dopenai.model.name="gpt-4" YourApplication
```

#### Option 3: Configuration File
Create a `forage-model-open-ai.properties` file in your classpath:

```properties
openai.api.key=sk-...
openai.model.name=gpt-4
openai.temperature=0.7
openai.max.tokens=1000
```

## Supported Models

### Current OpenAI Models

| Model Name | Description | Context Window | Cost Tier |
|------------|-------------|----------------|-----------|
| `gpt-3.5-turbo` | Fast, cost-effective model | 4,096 tokens | Low |
| `gpt-3.5-turbo-16k` | Extended context version | 16,384 tokens | Low-Medium |
| `gpt-4` | Most capable model | 8,192 tokens | High |
| `gpt-4-32k` | Extended context GPT-4 | 32,768 tokens | Very High |
| `gpt-4-turbo` | Latest GPT-4 with improvements | 128,000 tokens | High |
| `gpt-4o` | Optimized multimodal model | 128,000 tokens | Medium-High |
| `gpt-4o-mini` | Smaller, faster version | 128,000 tokens | Low-Medium |

### Model Selection Guide

#### For Cost-Effective Solutions
```bash
export OPENAI_MODEL_NAME="gpt-3.5-turbo"
```
- Best for: Simple tasks, high-volume applications
- Features: Fast responses, low cost

#### For Complex Tasks
```bash
export OPENAI_MODEL_NAME="gpt-4"
```
- Best for: Complex reasoning, detailed analysis
- Features: Superior capabilities, higher accuracy

#### For Long Documents
```bash
export OPENAI_MODEL_NAME="gpt-4-turbo"
```
- Best for: Long context tasks, document analysis
- Features: Large context window, latest improvements

#### For Multimodal Tasks
```bash
export OPENAI_MODEL_NAME="gpt-4o"
```
- Best for: Text and image processing
- Features: Multimodal capabilities, good performance

## Usage Examples

### Basic Configuration

```bash
# Minimal setup
export OPENAI_API_KEY="sk-your-api-key-here"
# Uses default model: gpt-3.5-turbo
```

### Advanced Configuration

```bash
# Full configuration example
export OPENAI_API_KEY="sk-your-api-key-here"
export OPENAI_MODEL_NAME="gpt-4"
export OPENAI_TEMPERATURE="0.7"
export OPENAI_MAX_TOKENS="1000"
export OPENAI_TOP_P="0.9"
export OPENAI_FREQUENCY_PENALTY="0.0"
export OPENAI_PRESENCE_PENALTY="0.0"
export OPENAI_LOG_REQUESTS="false"
export OPENAI_LOG_RESPONSES="false"
```

### Configuration File

Create `camel-forage-model-open-ai.properties`:

```properties
# Required
openai.api.key=sk-your-api-key-here

# Model selection
openai.model.name=gpt-4

# Response tuning
openai.temperature=0.7
openai.max.tokens=1500
openai.top.p=0.9

# Penalty settings
openai.frequency.penalty=0.1
openai.presence.penalty=0.1

# Logging (disable in production)
openai.log.requests=false
openai.log.responses=false
```

### Java Code Usage

```java
import io.kaoto.forage.models.chat.openai.OpenAIProvider;
import dev.langchain4j.model.chat.ChatModel;

// Create provider (configuration is loaded automatically)
OpenAIProvider provider = new OpenAIProvider();

// Get configured chat model
ChatModel model = provider.newModel();

// Use the model for chat operations
// (specific usage depends on your Forage setup)
```

### Custom Base URL (for Proxies/Compatible APIs)

```bash
# For OpenAI-compatible services
export OPENAI_BASE_URL="https://api.your-proxy.com/v1"
export OPENAI_API_KEY="your-proxy-api-key"
```

## Parameter Tuning Guide

### Temperature
Controls randomness and creativity:

- **0.0-0.2**: Very focused, deterministic responses
  ```bash
  export OPENAI_TEMPERATURE="0.1"  # For factual, consistent answers
  ```

- **0.3-0.7**: Balanced creativity and coherence
  ```bash
  export OPENAI_TEMPERATURE="0.7"  # Good general-purpose setting
  ```

- **0.8-1.0**: More creative and diverse responses
  ```bash
  export OPENAI_TEMPERATURE="0.9"  # For creative writing
  ```

- **1.0-2.0**: Highly creative but potentially inconsistent
  ```bash
  export OPENAI_TEMPERATURE="1.5"  # For brainstorming, experimental use
  ```

### Max Tokens
Controls response length:

```bash
# Short responses
export OPENAI_MAX_TOKENS="100"

# Medium responses
export OPENAI_MAX_TOKENS="500"

# Long responses
export OPENAI_MAX_TOKENS="2000"

# Let model decide (don't set max_tokens)
# unset OPENAI_MAX_TOKENS
```

### Top-P (Nucleus Sampling)
Controls vocabulary diversity:

```bash
# Conservative vocabulary
export OPENAI_TOP_P="0.5"

# Balanced (recommended)
export OPENAI_TOP_P="0.9"

# Full vocabulary
export OPENAI_TOP_P="1.0"
```

### Penalties
Control repetition and topic diversity:

```bash
# Reduce repetition
export OPENAI_FREQUENCY_PENALTY="0.5"

# Encourage new topics
export OPENAI_PRESENCE_PENALTY="0.5"

# Default (no penalties)
export OPENAI_FREQUENCY_PENALTY="0.0"
export OPENAI_PRESENCE_PENALTY="0.0"
```

## Security Considerations

### API Key Security
- **Never commit API keys to version control**
- Use environment variables or secure configuration management
- Rotate API keys regularly
- Monitor API usage for unauthorized access

### Best Practices
```bash
# Good: Using environment variables
export OPENAI_API_KEY="sk-your-key-here"

# Bad: Hardcoding in configuration files
# api-key=sk-...  # Don't commit this!
```

### Production Security
- Use secure secrets management (e.g., AWS Secrets Manager, HashiCorp Vault)
- Implement proper access controls and logging
- Monitor API usage and costs
- Set up usage alerts and rate limiting

### Request/Response Logging
```bash
# Enable only for debugging (not in production)
export OPENAI_LOG_REQUESTS="true"
export OPENAI_LOG_RESPONSES="true"

# Disable in production to avoid logging sensitive data
export OPENAI_LOG_REQUESTS="false"
export OPENAI_LOG_RESPONSES="false"
```

## Cost Management

### Understanding Costs
OpenAI charges based on token usage:
- **Input tokens**: Text you send to the model
- **Output tokens**: Text the model generates
- Different models have different pricing per token

### Cost Optimization Strategies

#### 1. Choose the Right Model
```bash
# For simple tasks
export OPENAI_MODEL_NAME="gpt-3.5-turbo"  # Most cost-effective

# For complex tasks only when needed
export OPENAI_MODEL_NAME="gpt-4"  # Higher cost but better quality
```

#### 2. Control Response Length
```bash
# Limit token usage
export OPENAI_MAX_TOKENS="500"  # Prevents overly long responses
```

#### 3. Optimize Temperature
```bash
# Lower temperature can reduce need for multiple attempts
export OPENAI_TEMPERATURE="0.3"
```

#### 4. Monitor Usage
- Set up billing alerts in OpenAI dashboard
- Monitor token usage regularly
- Implement usage tracking in your application

## Troubleshooting

### Common Issues

#### 1. Authentication Errors
```
Error: Missing OpenAI API key
```
**Solution**: Verify your API key is set correctly
```bash
echo $OPENAI_API_KEY  # Should display your key (starting with sk-)
```

#### 2. Rate Limiting
```
Error: Rate limit exceeded
```
**Solutions**:
- Implement exponential backoff
- Reduce request frequency
- Consider upgrading your OpenAI plan

#### 3. Token Limit Exceeded
```
Error: Token limit exceeded
```
**Solutions**:
```bash
# Reduce max tokens
export OPENAI_MAX_TOKENS="500"

# Use a model with larger context window
export OPENAI_MODEL_NAME="gpt-4-turbo"
```

#### 4. Invalid Model
```
Error: Model not found
```
**Solution**: Check model availability and spelling
```bash
# Use a known available model
export OPENAI_MODEL_NAME="gpt-3.5-turbo"
```

#### 5. Insufficient Credits
```
Error: Insufficient quota/credits
```
**Solution**: Add credits or set up billing in OpenAI dashboard

### Debugging Configuration

```bash
# Check all OpenAI environment variables
env | grep OPENAI

# Test with minimal configuration
export OPENAI_API_KEY="sk-your-key"
export OPENAI_MODEL_NAME="gpt-3.5-turbo"
unset OPENAI_TEMPERATURE
unset OPENAI_MAX_TOKENS
# ... unset other optional variables
```

### Verification Steps

1. **Verify API Key Format**: Should start with `sk-`
2. **Check OpenAI Status**: Visit [OpenAI Status Page](https://status.openai.com/)
3. **Test in OpenAI Playground**: Verify your key works directly with OpenAI
4. **Check Billing**: Ensure you have available credits

## Rate Limits and Best Practices

### Understanding Rate Limits
- Rate limits vary by model and account tier
- Measured in requests per minute and tokens per minute
- Higher-tier accounts have higher limits

### Best Practices
```java
// Implement retry logic with exponential backoff
// Monitor usage to stay within limits
// Consider request batching for efficiency
// Cache responses when appropriate
```

### Handling Rate Limits
```bash
# If hitting rate limits frequently, consider:
# 1. Upgrading your OpenAI plan
# 2. Implementing request queuing
# 3. Using less demanding models
export OPENAI_MODEL_NAME="gpt-3.5-turbo"  # Has higher rate limits than GPT-4
```

## Advanced Use Cases

### Custom Instructions
```bash
# While not directly configurable, you can influence behavior through model selection
export OPENAI_MODEL_NAME="gpt-4"  # Better at following complex instructions
```

### Proxy/Gateway Support
```bash
# For corporate environments or API gateways
export OPENAI_BASE_URL="https://your-api-gateway.com/openai/v1"
export OPENAI_API_KEY="your-gateway-token"
```

### Multi-Environment Setup
```bash
# Development
export OPENAI_MODEL_NAME="gpt-3.5-turbo"
export OPENAI_TEMPERATURE="0.7"

# Production
export OPENAI_MODEL_NAME="gpt-4"
export OPENAI_TEMPERATURE="0.3"
export OPENAI_MAX_TOKENS="1000"
```