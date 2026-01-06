# Forage - Amazon Bedrock Model Provider

Amazon Bedrock integration for Forage, providing access to foundation models through AWS.

## Overview

Amazon Bedrock is a managed service offering access to foundation models from multiple providers through a unified API. This provider enables integration with Bedrock's model catalog within the Forage framework.

## Supported Models

### Anthropic Claude
- **Claude 3.5 Sonnet** - `anthropic.claude-3-5-sonnet-20240620-v1:0`
- **Claude 3 Opus** - `anthropic.claude-3-opus-20240229-v1:0`
- **Claude 3 Sonnet** - `anthropic.claude-3-sonnet-20240229-v1:0`
- **Claude 3 Haiku** - `anthropic.claude-3-haiku-20240307-v1:0`
- **Claude 2.1** - `anthropic.claude-v2:1`
- **Claude 2.0** - `anthropic.claude-v2`

### Meta Llama
- **Llama 3.1 405B** - `meta.llama3-1-405b-instruct-v1:0`
- **Llama 3.1 70B** - `meta.llama3-1-70b-instruct-v1:0`
- **Llama 3.1 8B** - `meta.llama3-1-8b-instruct-v1:0`
- **Llama 3 70B** - `meta.llama3-70b-instruct-v1:0`
- **Llama 3 8B** - `meta.llama3-8b-instruct-v1:0`
- **Llama 2 70B** - `meta.llama2-70b-chat-v1`
- **Llama 2 13B** - `meta.llama2-13b-chat-v1`

### Amazon Titan
- **Titan Text Premier** - `amazon.titan-text-premier-v1:0`
- **Titan Text Express** - `amazon.titan-text-express-v1`
- **Titan Text Lite** - `amazon.titan-text-lite-v1`

### Cohere Command
- **Command R+** - `cohere.command-r-plus-v1:0`
- **Command R** - `cohere.command-r-v1:0`
- **Command Text** - `cohere.command-text-v14`
- **Command Light Text** - `cohere.command-light-text-v14`

### Mistral AI
- **Mistral Large** - `mistral.mistral-large-2402-v1:0`
- **Mistral 7B** - `mistral.mistral-7b-instruct-v0:2`
- **Mixtral 8x7B** - `mistral.mixtral-8x7b-instruct-v0:1`

## Configuration

### Environment Variables

```bash
# Required
export BEDROCK_MODEL_ID="anthropic.claude-3-5-sonnet-20240620-v1:0"

# Optional
export BEDROCK_REGION="us-east-1"
export BEDROCK_ACCESS_KEY_ID="your-access-key"
export BEDROCK_SECRET_ACCESS_KEY="your-secret-key"
export BEDROCK_TEMPERATURE="0.7"
export BEDROCK_MAX_TOKENS="2048"
export BEDROCK_TOP_P="0.9"
```

### System Properties

```bash
-Dbedrock.model.id=anthropic.claude-3-5-sonnet-20240620-v1:0
-Dbedrock.region=us-east-1
-Dbedrock.temperature=0.7
-Dbedrock.max.tokens=2048
```

### Configuration File

Create `forage-model-bedrock.properties` in your classpath:

```properties
bedrock.model.id=anthropic.claude-3-5-sonnet-20240620-v1:0
bedrock.region=us-east-1
bedrock.temperature=0.7
bedrock.max.tokens=2048
```

### Named Configurations

Multiple model configurations can be used concurrently:

```bash
# Default configuration
export BEDROCK_MODEL_ID="anthropic.claude-3-haiku-20240307-v1:0"

# Named configuration for complex tasks
export smart.bedrock.model.id="anthropic.claude-3-5-sonnet-20240620-v1:0"
export smart.bedrock.temperature="0.3"

# Named configuration for code generation
export code.bedrock.model.id="anthropic.claude-3-opus-20240229-v1:0"
export code.bedrock.temperature="0.2"
```

## AWS Authentication

Authentication methods in order of precedence:

1. **Explicit credentials** via `BEDROCK_ACCESS_KEY_ID` and `BEDROCK_SECRET_ACCESS_KEY`
2. **AWS environment variables** - `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`
3. **AWS credentials file** - `~/.aws/credentials`
4. **IAM role** - For EC2, ECS, Lambda, EKS workloads

## Regional Availability

Bedrock models are available in select AWS regions. Model availability varies by region:

- `us-east-1` (N. Virginia)
- `us-west-2` (Oregon)
- `ap-southeast-1` (Singapore)
- `ap-northeast-1` (Tokyo)
- `eu-central-1` (Frankfurt)
- `eu-west-3` (Paris)

Refer to the [AWS Bedrock documentation](https://docs.aws.amazon.com/bedrock/latest/userguide/models-regions.html) for current availability.

## IAM Permissions

### Minimal Required Permissions

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "bedrock:InvokeModel"
            ],
            "Resource": "arn:aws:bedrock:*::foundation-model/*"
        }
    ]
}
```

### Restricted to Specific Models

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "bedrock:InvokeModel"
            ],
            "Resource": [
                "arn:aws:bedrock:us-east-1::foundation-model/anthropic.claude-3-5-sonnet-20240620-v1:0",
                "arn:aws:bedrock:us-east-1::foundation-model/anthropic.claude-3-haiku-20240307-v1:0"
            ]
        }
    ]
}
```

## Model Access

Bedrock models require opt-in before use:

1. Navigate to AWS Console → Amazon Bedrock → Model access
2. Select "Manage model access"
3. Choose desired models
4. Submit access request
5. Wait for approval (typically instant for most models)

## Usage

### Basic Camel Route

```java
from("direct:chat")
    .to("langchain4j-agent:bedrock?agentFactory=#class:io.kaoto.forage.agent.factory.MultiAgentFactory")
    .log("Response: ${body}");
```

### Multiple Model Configuration

```java
System.setProperty("fast.bedrock.model.id", "anthropic.claude-3-haiku-20240307-v1:0");
System.setProperty("advanced.bedrock.model.id", "anthropic.claude-3-5-sonnet-20240620-v1:0");

from("direct:fast-chat")
    .setHeader("agentId", constant("fast"))
    .to("langchain4j-agent:fast?agentFactory=#class:io.kaoto.forage.agent.factory.MultiAgentFactory");

from("direct:advanced-chat")
    .setHeader("agentId", constant("advanced"))
    .to("langchain4j-agent:advanced?agentFactory=#class:io.kaoto.forage.agent.factory.MultiAgentFactory");
```

### Using BedrockModelId Enum

```java
System.setProperty("bedrock.model.id", BedrockModelId.CLAUDE_3_5_SONNET.getModelId());
System.setProperty("fast.bedrock.model.id", BedrockModelId.CLAUDE_3_HAIKU.getModelId());
System.setProperty("llama.bedrock.model.id", BedrockModelId.LLAMA_3_1_70B.getModelId());
```

## Configuration Reference

| Property | Required | Default | Description |
|----------|----------|---------|-------------|
| `bedrock.region` | No | `us-east-1` | AWS region |
| `bedrock.model.id` | Yes | - | Bedrock model identifier |
| `bedrock.access.key.id` | No | - | AWS access key |
| `bedrock.secret.access.key` | No | - | AWS secret key |
| `bedrock.temperature` | No | model default | Sampling temperature (0.0-1.0) |
| `bedrock.max.tokens` | No | model default | Maximum response tokens |
| `bedrock.top.p` | No | model default | Nucleus sampling (0.0-1.0) |

## Troubleshooting

### Model Not Found

**Symptoms:** Error message indicating model cannot be found

**Solutions:**
- Verify model availability in your region
- Check model access has been granted in AWS Console
- Confirm model ID is correct (case-sensitive)

### Authentication Failures

**Symptoms:** Access denied or credential errors

**Solutions:**
- Verify AWS credentials are configured
- Check IAM permissions include `bedrock:InvokeModel`
- Ensure credentials are valid and not expired
- Test with: `aws bedrock list-foundation-models`

### Access Denied to Specific Model

**Symptoms:** Permission error for specific model

**Solutions:**
- Request model access in AWS Console → Bedrock → Model access
- Wait for approval to complete
- Verify IAM policy allows access to specific model ARN

### Rate Limiting

**Symptoms:** Throttling errors or quota exceeded messages

**Solutions:**
- Implement retry logic with exponential backoff
- Request quota increases via AWS Support
- Distribute requests across multiple regions
- Review and optimize request patterns

## Dependencies

This module requires:

- Java 17+
- Apache Camel 4.14.0+
- LangChain4j with Bedrock support
- AWS SDK for Java 2.37.2+

## Additional Resources

- [AWS Bedrock Documentation](https://docs.aws.amazon.com/bedrock/)
- [Bedrock Pricing](https://aws.amazon.com/bedrock/pricing/)
- [Model Access Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html)
- [AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/)

## License

This module is part of the Apache Forage project and is licensed under the Apache License 2.0.
