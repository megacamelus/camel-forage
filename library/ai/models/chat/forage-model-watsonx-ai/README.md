# IBM Watsonx.ai Model Provider

This module provides integration with IBM Watsonx.ai for the Forage framework, enabling you to use IBM's enterprise AI platform and foundation models in your applications.

## Configuration

The Watsonx.ai provider supports configuration through environment variables, system properties, and configuration files.

### Required Configuration

- **API Key**: Your IBM Cloud API key (required)
- **URL**: The Watsonx.ai service URL (required)
- **Project ID**: Your Watsonx.ai project ID (required)

### Optional Configuration

- **Model Name**: The specific model to use (defaults to "llama-3-405b-instruct")
- **Temperature**: Controls randomness (0.0-2.0)
- **Max New Tokens**: Maximum new tokens in response
- **Top-P**: Nucleus sampling parameter (0.0-1.0)
- **Top-K**: Top-k sampling parameter
- **Random Seed**: Random seed for reproducible results
- **Repetition Penalty**: Penalty for repetitive content (1.0-2.0)
- **Min New Tokens**: Minimum new tokens in response
- **Stop Sequences**: Stop sequences for response generation
- **Timeout**: Request timeout in seconds
- **Max Retries**: Maximum retry attempts
- **Request/Response Logging**: Enable detailed logging

### Environment Variables

```bash
# Required Environment variables
export WATSONXAI_API_KEY="your-ibm-cloud-api-key"
export WATSONXAI_URL="https://us-south.ml.cloud.ibm.com"
export WATSONXAI_PROJECT_ID="your-project-id"

# Optional Environment variables
export WATSONXAI_MODEL_NAME="llama-3-405b-instruct"
export WATSONXAI_TEMPERATURE="0.7"
export WATSONXAI_MAX_NEW_TOKENS="2048"
export WATSONXAI_TOP_P="0.9"
export WATSONXAI_TOP_K="50"
export WATSONXAI_RANDOM_SEED="12345"
export WATSONXAI_REPETITION_PENALTY="1.1"
export WATSONXAI_MIN_NEW_TOKENS="1"
export WATSONXAI_STOP_SEQUENCES="Human:,Assistant:"
export WATSONXAI_TIMEOUT="60"
export WATSONXAI_MAX_RETRIES="3"
export WATSONXAI_LOG_REQUESTS_AND_RESPONSES="false"
```

### System Properties

```bash
# Required System properties
-Dwatsonxai.api.key=your-ibm-cloud-api-key
-Dwatsonxai.url=https://us-south.ml.cloud.ibm.com
-Dwatsonxai.project.id=your-project-id

# Optional System properties
-Dwatsonxai.model.name=llama-3-405b-instruct
-Dwatsonxai.temperature=0.7
-Dwatsonxai.max.new.tokens=2048
-Dwatsonxai.top.p=0.9
-Dwatsonxai.top.k=50
-Dwatsonxai.random.seed=12345
-Dwatsonxai.repetition.penalty=1.1
-Dwatsonxai.min.new.tokens=1
-Dwatsonxai.stop.sequences=Human:,Assistant:
-Dwatsonxai.timeout=60
-Dwatsonxai.max.retries=3
-Dwatsonxai.log.requests.and.responses=false
```

### Configuration File

Create a `forage-model-watsonx-ai.properties` file in your classpath:

```properties
# Required
watsonxai.api.key=your-ibm-cloud-api-key
watsonxai.url=https://us-south.ml.cloud.ibm.com
watsonxai.project.id=your-project-id

# Optional
watsonxai.model.name=llama-3-405b-instruct
watsonxai.temperature=0.7
watsonxai.max.new.tokens=2048
watsonxai.top.p=0.9
watsonxai.top.k=50
watsonxai.random.seed=12345
watsonxai.repetition.penalty=1.1
watsonxai.min.new.tokens=1
watsonxai.stop.sequences=Human:,Assistant:
watsonxai.timeout=60
watsonxai.max.retries=3
watsonxai.log.requests.and.responses=false
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
| `api-key` | String | *Required* | IBM Cloud API key |
| `url` | String | *Required* | Watsonx.ai service URL |
| `project-id` | String | *Required* | Watsonx.ai project ID |
| `model-name` | String | "llama-3-405b-instruct" | Model name to use |
| `temperature` | Double | None | Controls randomness (0.0-2.0) |
| `max-new-tokens` | Integer | None | Maximum new tokens in response |
| `top-p` | Double | None | Nucleus sampling parameter (0.0-1.0) |
| `top-k` | Integer | None | Top-k sampling parameter |
| `random-seed` | Integer | None | Random seed for reproducible results |
| `repetition-penalty` | Double | None | Penalty for repetitive content (1.0-2.0) |
| `min-new-tokens` | Integer | None | Minimum new tokens in response |
| `stop-sequences` | String | None | Stop sequences (comma-separated) |
| `timeout` | Integer | None | Request timeout in seconds |
| `max-retries` | Integer | None | Maximum retry attempts |
| `log-requests-and-responses` | Boolean | None | Enable request/response logging |

## Available Models

Watsonx.ai offers several foundation models with different capabilities:

### Llama Models
- **llama-3-405b-instruct**: Largest Llama 3 model with highest capabilities
- **llama-3-70b-instruct**: Medium Llama 3 model with balanced performance
- **llama-3-8b-instruct**: Small, fast Llama 3 model for simpler tasks

### IBM Granite Models
- **granite-13b-chat-v2**: IBM Granite model optimized for chat
- **granite-13b-instruct-v2**: IBM Granite model optimized for instructions
- **granite-20b-multilingual**: Multilingual Granite model

### Other Models
- **mistral-large**: Mistral's large model
- **mixtral-8x7b-instruct-v01**: Mixtral sparse mixture of experts model

## Service URLs by Region

Choose the appropriate URL for your IBM Cloud region:

- **US South**: `https://us-south.ml.cloud.ibm.com`
- **EU Germany**: `https://eu-de.ml.cloud.ibm.com`
- **Japan Tokyo**: `https://jp-tok.ml.cloud.ibm.com`

## Setup Instructions

### 1. IBM Cloud Setup

1. Create an IBM Cloud account at [cloud.ibm.com](https://cloud.ibm.com)
2. Create a Watsonx.ai service instance
3. Create an API key in IBM Cloud IAM
4. Create or select a project in Watsonx.ai
5. Note your project ID from the Watsonx.ai console

### 2. Configuration

Set the required environment variables:

```bash
export WATSONXAI_API_KEY="your-ibm-cloud-api-key"
export WATSONXAI_URL="https://us-south.ml.cloud.ibm.com"  # Use your region
export WATSONXAI_PROJECT_ID="your-project-id"
```

### 3. Usage in Camel Routes

```java
from("direct:watsonx-chat")
    .to("langchain4j-agent:watsonx-agent?agentFactory=#class:io.kaoto.forage.agent.factory.MultiAgentFactory")
    .log("Watsonx.ai Response: ${body}");
```

## Security Considerations

- Never commit API keys to version control
- Use IBM Cloud IAM for proper access control
- Use environment variables or secure configuration management for production
- Monitor API usage and costs through the IBM Cloud console
- Consider using IBM Cloud service credentials for enhanced security