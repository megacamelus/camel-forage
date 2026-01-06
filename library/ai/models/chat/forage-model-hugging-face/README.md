# HuggingFace Model Provider

This module provides integration with HuggingFace Inference API for the Forage framework, enabling you to use various open-source models hosted on HuggingFace.

## Configuration

The HuggingFace provider supports configuration through environment variables, system properties, and configuration files.

### Required Configuration

- **API Key**: Your HuggingFace API token for authentication

### Environment Variables

```bash
# Required Environment variables
export HUGGINGFACE_API_KEY="hf_your-api-key-here"

# Optional Environment variables
export HUGGINGFACE_MODEL_ID="microsoft/DialoGPT-medium"
export HUGGINGFACE_TEMPERATURE="0.7"
export HUGGINGFACE_MAX_NEW_TOKENS="256"
export HUGGINGFACE_WAIT_FOR_MODEL="true"
export HUGGINGFACE_TIMEOUT="60"
```

### System Properties

```bash
# Required System properties
-Dhuggingface.api.key=hf_your-api-key-here

# Optional System properties
-Dhuggingface.model.id=microsoft/DialoGPT-medium
-Dhuggingface.temperature=0.7
-Dhuggingface.max.new.tokens=256
-Dhuggingface.wait.for.model=true
-Dhuggingface.timeout=60
```

### Configuration File

Create a `forage-model-hugging-face.properties` file in your classpath:

```properties
# Required
huggingface.api.key=hf_your-api-key-here

# Optional
huggingface.model.id=microsoft/DialoGPT-medium
huggingface.temperature=0.7
huggingface.max.new.tokens=256
huggingface.wait.for.model=true
huggingface.timeout=60
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
| `api-key` | String | *Required* | HuggingFace API token |
| `model-id` | String | None | HuggingFace model identifier |
| `temperature` | Double | None | Controls randomness (0.0-2.0) |
| `max-new-tokens` | Integer | None | Maximum tokens to generate |
| `wait-for-model` | Boolean | false | Wait for model to load if cold |
| `timeout` | Integer | 60 | Request timeout in seconds |

## Popular Models

### Conversational Models
- **microsoft/DialoGPT-medium** - Medium-sized conversational model
- **microsoft/DialoGPT-large** - Large conversational model
- **facebook/blenderbot-400M-distill** - Facebook's BlenderBot

### Instruction-Following Models
- **google/flan-t5-base** - Google's FLAN-T5 model
- **google/flan-t5-large** - Larger FLAN-T5 variant
- **microsoft/Phi-3.5-mini-instruct** - Microsoft's Phi model

### Code Generation Models
- **codellama/CodeLlama-7b-Instruct-hf** - Code Llama for instruction following
- **WizardLM/WizardCoder-15B-V1.0** - Code generation specialist

## Getting Started

1. Create a HuggingFace account at https://huggingface.co/
2. Generate an API token in your HuggingFace settings
3. Set the `HUGGINGFACE_API_KEY` environment variable
4. Optionally specify a model ID (browse available models at https://huggingface.co/models)

## Model Selection Tips

### For General Conversation
```bash
export HUGGINGFACE_MODEL_ID="microsoft/DialoGPT-medium"
```

### For Instruction Following
```bash
export HUGGINGFACE_MODEL_ID="google/flan-t5-base"
```

### For Code Generation
```bash
export HUGGINGFACE_MODEL_ID="codellama/CodeLlama-7b-Instruct-hf"
```

## Important Notes

- **Model Availability**: Not all models support the Inference API
- **Cold Start**: Models may take time to load if not recently used
- **Rate Limits**: Free tier has usage limitations
- **Model Types**: This provider works with text-generation and conversational models

## Security Considerations

- Never commit API tokens to version control
- Use environment variables or secure configuration management in production
- Monitor your HuggingFace usage to avoid unexpected charges

## Troubleshooting

### Common Issues

1. **Model Not Found**: Verify the model ID exists and supports Inference API
2. **Cold Start Timeout**: Set `wait-for-model=true` for models that need loading time
3. **Rate Limiting**: Consider upgrading to HuggingFace Pro for higher limits
4. **Authentication**: Ensure your API token is valid and has the necessary permissions