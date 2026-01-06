# Forage Google Gemini Model

This module provides integration with Google's Gemini AI models for the Forage framework.

## Overview

The Google Gemini model integration allows you to use Google's state-of-the-art AI models with Forage. Gemini models offer advanced capabilities including text generation, code generation, and multimodal understanding.

## Dependencies

Add this dependency to your Maven project:

```xml
<dependency>
    <groupId>io.kaoto.forage</groupId>
    <artifactId>forage-model-google-gemini</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Configuration

The Google Gemini provider is configured through environment variables, system properties, or configuration files.

### Required Configuration

| Environment Variable | System Property | Description | Default |
|---------------------|-----------------|-------------|---------|
| `GOOGLE_API_KEY` | `google.api.key` | Your Google AI API key | **Required** |
| `GOOGLE_MODEL_NAME` | `google.model.name` | Name of the Gemini model to use | **Required** |

### Configuration Sources Priority

Configuration values are resolved in the following order of precedence:

1. Environment variables
2. System properties
3. `forage-model-google-gemini.properties` file in classpath

## Getting Started

### Prerequisites

1. **Google AI Studio Account**: Create an account at [Google AI Studio](https://aistudio.google.com/)
2. **API Key**: Generate an API key from the Google AI Studio console
3. **Model Access**: Ensure you have access to the Gemini models you want to use

### API Key Setup

#### Option 1: Environment Variable (Recommended)
```bash
export GOOGLE_API_KEY="your-api-key-here"
export GOOGLE_MODEL_NAME="gemini-pro"
```

#### Option 2: System Properties
```bash
java -Dgoogle.api.key="your-api-key-here" -Dgoogle.model.name="gemini-pro" YourApplication
```

#### Option 3: Configuration File
Create a `forage-model-google-gemini.properties` file in your classpath:

```properties
google.api.key=your-api-key-here
google.model.name=gemini-pro
```

## Supported Models

### Current Gemini Models

| Model Name | Description | Use Cases |
|------------|-------------|-----------|
| `gemini-pro` | High-performance model for text generation | General text tasks, conversation, content creation |
| `gemini-pro-vision` | Multimodal model with image understanding | Image analysis, visual Q&A, multimodal tasks |
| `gemini-1.5-pro` | Latest version with enhanced capabilities | Advanced reasoning, longer context, improved performance |
| `gemini-1.5-flash` | Faster, more efficient model | Quick responses, high-throughput applications |

### Model Capabilities

- **Text Generation**: Natural language understanding and generation
- **Code Generation**: Programming assistance and code completion
- **Multimodal Understanding**: Image and text analysis (vision models)
- **Long Context**: Support for extended conversations and documents
- **Reasoning**: Advanced logical reasoning and problem-solving

## Usage Example

### Basic Setup

```java
import io.kaoto.forage.models.chat.google.GoogleGeminiProvider;
import dev.langchain4j.model.chat.ChatModel;

// Create provider (configuration is loaded automatically)
GoogleGeminiProvider provider = new GoogleGeminiProvider();

// Get configured chat model
ChatModel model = provider.newModel();

// Use the model for chat operations
// (specific usage depends on your Forage setup)
```

### Environment Configuration Example

```bash
# Basic configuration
export GOOGLE_API_KEY="AIzaSyC..."  # Your actual API key
export GOOGLE_MODEL_NAME="gemini-pro"

# For multimodal tasks
export GOOGLE_MODEL_NAME="gemini-pro-vision"

# For latest capabilities
export GOOGLE_MODEL_NAME="gemini-1.5-pro"
```

### Configuration File Example

Create `forage-model-google-gemini.properties`:

```properties
# Required configuration
api-key=AIzaSyC...
model-name=gemini-pro

# Alternative models
# model-name=gemini-pro-vision
# model-name=gemini-1.5-pro
# model-name=gemini-1.5-flash
```

## Model Selection Guide

### For General Text Tasks
```bash
export GOOGLE_MODEL_NAME="gemini-pro"
```
- Best for: Conversation, content creation, general Q&A
- Features: High-quality text generation, good reasoning

### For Multimodal Tasks
```bash
export GOOGLE_MODEL_NAME="gemini-pro-vision"
```
- Best for: Image analysis, visual question answering
- Features: Understands both text and images

### For Advanced Tasks
```bash
export GOOGLE_MODEL_NAME="gemini-1.5-pro"
```
- Best for: Complex reasoning, long documents, advanced tasks
- Features: Enhanced capabilities, longer context window

### For High-Throughput Applications
```bash
export GOOGLE_MODEL_NAME="gemini-1.5-flash"
```
- Best for: Quick responses, batch processing
- Features: Faster inference, efficient processing

## Security Considerations

### API Key Security
- **Never commit API keys to version control**
- Use environment variables or secure configuration management
- Rotate API keys regularly
- Monitor API usage for unauthorized access

### Best Practices
```bash
# Good: Using environment variables
export GOOGLE_API_KEY="your-key-here"

# Bad: Hardcoding in configuration files committed to git
# api-key=AIzaSyC...  # Don't do this!
```

### Production Security
- Use secure secrets management (e.g., AWS Secrets Manager, Azure Key Vault)
- Implement proper access controls
- Monitor API usage and set up alerts
- Use least-privilege principles

## Troubleshooting

### Common Issues

#### 1. Authentication Errors
```
Error: Missing Google API key
```
**Solution**: Verify your API key is set correctly
```bash
echo $GOOGLE_API_KEY  # Should display your key
```

#### 2. Model Not Found
```
Error: Model not available
```
**Solution**: Check model name and availability
```bash
# Verify you're using a supported model name
export GOOGLE_MODEL_NAME="gemini-pro"  # Try basic model first
```

#### 3. Rate Limiting
```
Error: Rate limit exceeded
```
**Solution**: Implement retry logic and respect rate limits

#### 4. Invalid Model Name
```
Error: Missing Google model name
```
**Solution**: Ensure both API key and model name are configured
```bash
export GOOGLE_API_KEY="your-key-here"
export GOOGLE_MODEL_NAME="gemini-pro"
```

### Verification Steps

1. **Check Configuration**:
   ```bash
   echo "API Key: $GOOGLE_API_KEY"
   echo "Model: $GOOGLE_MODEL_NAME"
   ```

2. **Test API Access**: Use Google AI Studio to verify your API key works

3. **Check Model Availability**: Ensure the model you're trying to use is available in your region

## Rate Limits and Quotas

### Understanding Limits
- Google AI has rate limits and quotas for API usage
- Limits vary by model and pricing tier
- Monitor your usage in Google AI Studio

### Best Practices
- Implement exponential backoff for retries
- Cache responses when appropriate
- Monitor usage to avoid unexpected charges
- Consider request batching for efficiency

## Cost Optimization

### Tips for Reducing Costs
1. **Choose the right model**: Use `gemini-1.5-flash` for simpler tasks
2. **Optimize prompts**: Shorter, clearer prompts reduce token usage
3. **Cache responses**: Avoid repeated identical requests
4. **Monitor usage**: Set up billing alerts and usage monitoring

### Model Cost Comparison
- **gemini-1.5-flash**: Most cost-effective for simple tasks
- **gemini-pro**: Balanced cost and capability
- **gemini-1.5-pro**: Premium pricing for advanced features

## Advanced Configuration

### Custom Model Parameters
The current implementation uses fixed parameters optimized for general use:
- Temperature: 1.0 (balanced creativity)
- Timeout: 60 seconds
- Request/Response Logging: Enabled

### Future Enhancements
Future versions may support configurable parameters like:
- Custom temperature settings
- Adjustable timeouts
- Safety settings configuration
- Custom system instructions