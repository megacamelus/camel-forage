# Multi-Agent Development Guide with Forage

This guide explains how to use the Forage library to create sophisticated multi-agent systems using Apache Camel and Kaoto.

## Overview

Forage provides an opinionated library of beans for Apache Camel that simplifies AI integrations, particularly for creating multi-agent systems. The library offers pre-configured components that follow best practices for AI agent orchestration.

## Key Components

### Agent Factories

- **MultiAgentFactory**: Agent orchestration with ServiceLoader discovery, supporting both single-agent and multi-agent configurations

### Agent Types

- **SimpleAgent**: Memory-aware agent with conversation persistence
- **MemorylessAgent**: Stateless agent for independent interactions

### Model Providers

- **GoogleGeminiProvider**: Google Gemini AI models
- **OllamaProvider**: Local Ollama model integration
- **OpenAIProvider**: OpenAI GPT models

### Memory Providers

- **MessageWindowChatMemoryFactory**: Message window memory with persistence
- **InfinispanChatMemoryFactory**: Distributed memory storage
- **RedisChatMemoryFactory**: Redis-based memory for scalability

## Multi-Agent Configuration

### 1. Configuration Files

Create configuration files for each component:

#### forage-agent-factory.properties
```properties
# Define multi agents
multi.agent.names=google,ollama

# Google Agent Configuration
google.provider.agent.class=io.kaoto.forage.agent.simple.SimpleAgent
google.provider.model.factory.class=io.kaoto.forage.models.chat.google.GoogleGeminiProvider
google.provider.features=memory
google.provider.features.memory.factory.class=io.kaoto.forage.memory.chat.messagewindow.MessageWindowChatMemoryBeanProvider

# Ollama Agent Configuration
ollama.provider.agent.class=io.kaoto.forage.agent.memoryless.MemorylessAgent
ollama.provider.model.factory.class=io.kaoto.forage.models.chat.ollama.OllamaProvider
ollama.provider.features=memoryless
ollama.provider.features.memory.factory.class=io.kaoto.forage.memory.chat.messagewindow.MessageWindowChatMemoryBeanProvider
```

#### forage-model-google-gemini.properties
```properties
# Google API Configuration
google.api.key=your-google-api-key-here
google.model.name=gemini-2.5-flash
```

#### forage-model-ollama.properties
```properties
# Ollama Configuration
ollama.base.url=http://localhost:11434
ollama.model.name=llama3.1:latest
```

### 2. Agent ID Source Configuration

The MultiAgentFactory supports different strategies for extracting agent IDs from exchanges. By default, it uses the route ID, but you can configure it to extract the agent ID from headers, properties, or variables.

#### Available Agent ID Sources

| Source Type | Description | Configuration Property | Additional Configuration Required |
|-------------|-------------|----------------------|----------------------------------|
| `route` | Extract from route ID (default) | `multi.agent.id.source=route` | None |
| `header` | Extract from exchange header | `multi.agent.id.source=header` | `multi.agent.id.source.header=HeaderName` |
| `property` | Extract from exchange property | `multi.agent.id.source=property` | `multi.agent.id.source.property=PropertyName` |
| `variable` | Extract from exchange variable | `multi.agent.id.source=variable` | `multi.agent.id.source.variable=VariableName` |

#### Configuration Examples

**Route ID Source (Default):**
```properties
# Uses the route ID to determine which agent to use
multi.agent.id.source=route
```

**Header Source:**
```properties
# Extract agent ID from the "AgentType" header
multi.agent.id.source=header
multi.agent.id.source.header=AgentType
```

**Property Source:**
```properties
# Extract agent ID from the "agent.name" exchange property
multi.agent.id.source=property
multi.agent.id.source.property=agent.name
```

**Variable Source:**
```properties
# Extract agent ID from the "selectedAgent" exchange variable
multi.agent.id.source=variable
multi.agent.id.source.variable=selectedAgent
```

#### Route Examples with Different ID Sources

**Using Header for Agent Selection:**
```yaml
- route:
    id: dynamic-agent-header
    from:
      uri: direct:process-with-header
      steps:
        - setHeader:
            name: AgentType
            expression:
              simple: "${body.agentType}"
        - to:
            uri: langchain4j-agent:dynamic
            parameters:
              agentFactory: "#class:io.kaoto.forage.agent.factory.MultiAgentFactory"
        - log: "Response from ${header.AgentType}: ${body}"
```

**Using Property for Agent Selection:**
```yaml
- route:
    id: dynamic-agent-property
    from:
      uri: direct:process-with-property
      steps:
        - setProperty:
            name: agent.name
            expression:
              simple: "${body.complexity == 'high' ? 'google' : 'ollama'}"
        - to:
            uri: langchain4j-agent:dynamic
            parameters:
              agentFactory: "#class:io.kaoto.forage.agent.factory.MultiAgentFactory"
        - log: "Response from ${exchangeProperty.agent.name}: ${body}"
```

**Using Variable for Agent Selection:**
```yaml
- route:
    id: dynamic-agent-variable
    from:
      uri: direct:process-with-variable
      steps:
        - setVariable:
            name: selectedAgent
            expression:
              simple: "${body.userPreference}"
        - to:
            uri: langchain4j-agent:dynamic
            parameters:
              agentFactory: "#class:io.kaoto.forage.agent.factory.MultiAgentFactory"
        - log: "Response from ${variable.selectedAgent}: ${body}"
```

### 3. Camel Route Configuration

#### Single Agent Example (agent.camel.yaml)
```yaml
- route:
    id: single-agent-route
    from:
      uri: timer:yaml
      parameters:
        repeatCount: "1"
      steps:
        - setHeader:
            expression:
              simple:
                expression: "1"
            name: CamelLangChain4jAgentMemoryId
        - setBody:
            simple: give the details of user 123
        - to:
            uri: langchain4j-agent:test
            parameters:
              agentFactory: "#class:io.kaoto.forage.agent.factory.MultiAgentFactory"
              tags: users
        - log: ${body}

- route:
    id: tool-route
    from:
      uri: langchain4j-tools:userDb
      parameters:
        description: Query user database
        parameter.userId: string
        tags: users
      steps:
        - setBody:
            simple:
              expression: '{"name": "John Doe", "id": "123"}'
        - log: ${body}
```

#### Multi-Agent Example (multi-agent.camel.yaml)
```yaml
- route:
    id: google-agent
    description: Route using Google Gemini Agent
    from:
      uri: timer:google
      parameters:
        repeatCount: "1"
      steps:
        - setHeader:
            expression:
              simple:
                expression: "1"
            name: CamelLangChain4jAgentMemoryId
        - setBody:
            simple: give the details of user 123
        - to:
            uri: langchain4j-agent:google-test
            parameters:
              agentFactory: "#class:io.kaoto.forage.agent.factory.MultiAgentFactory"
              tags: users
        - log: "Google Agent Response: ${body}"

- route:
    id: ollama-agent
    description: Route using local Ollama Agent
    from:
      uri: timer:ollama
      parameters:
        repeatCount: "1"
      steps:
        - setHeader:
            expression:
              simple:
                expression: "1"
            name: CamelLangChain4jAgentMemoryId
        - setBody:
            simple: What is the timezone in Brasilia?
        - to:
            uri: langchain4j-agent:ollama-test
            parameters:
              agentFactory: "#class:io.kaoto.forage.agent.factory.MultiAgentFactory"
        - log: "Ollama Agent Response: ${body}"

# Shared tools available to all agents
- route:
    id: user-database-tool
    from:
      uri: langchain4j-tools:userDb
      parameters:
        description: Query user database
        parameter.userId: string
        tags: users
      steps:
        - setBody:
            simple:
              expression: '{"name": "John Doe", "id": "123"}'
        - log: "User DB Tool Response: ${body}"
```

## Running the Examples

### Prerequisites

1. **Camel JBang 4.14+**
2. **JBang 0.129.0+**
3. **API Keys**: Configure Google API key in `forage-model-google-gemini.properties`
4. **Ollama**: For local model integration, ensure Ollama is running on localhost:11434

### Single Agent Execution

```bash
camel run agent.camel.yaml \
  --dep=mvn:io.kaoto.forage:forage-agent-factories:1.0-SNAPSHOT \
  --dep=mvn:io.kaoto.forage:forage-agent:1.0-SNAPSHOT \
  --dep=mvn:io.kaoto.forage:forage-memory-message-window:1.0-SNAPSHOT \
  --dep=mvn:io.kaoto.forage:forage-model-google-gemini:1.0-SNAPSHOT \
  --dep=camel-langchain4j-agent
```

### Multi-Agent Execution

```bash
camel run multi-agent.camel.yaml \
  --dep=mvn:io.kaoto.forage:forage-agent-factories:1.0-SNAPSHOT \
  --dep=mvn:io.kaoto.forage:forage-agent:1.0-SNAPSHOT \
  --dep=mvn:io.kaoto.forage:forage-memory-message-window:1.0-SNAPSHOT \
  --dep=mvn:io.kaoto.forage:forage-model-google-gemini:1.0-SNAPSHOT \
  --dep=mvn:io.kaoto.forage:forage-model-ollama:1.0-SNAPSHOT \
  --dep=camel-langchain4j-agent
```

## Kaoto Integration

### Visual Route Design

Kaoto, the visual designer for Apache Camel, can be used to create and modify multi-agent routes visually:

1. **Import Camel YAML**: Load your `multi-agent.camel.yaml` into Kaoto
2. **Visual Editing**: Use the drag-and-drop interface to modify routes
3. **Agent Configuration**: Configure agent factories and parameters through the properties panel
4. **Export**: Generate updated YAML configurations for deployment

### Kaoto-Compatible Route Structure

When designing routes in Kaoto, ensure the following structure:

```yaml
# Route with proper IDs and descriptions for Kaoto
- route:
    id: unique-route-id
    description: Human-readable description
    from:
      id: from-component-id
      uri: component:name
      parameters:
        key: value
      steps:
        - processor:
            id: processor-id
        - to:
            id: to-component-id
            uri: langchain4j-agent:agent-name
            parameters:
              agentFactory: "#class:factory.class"
```

### Best Practices for Kaoto

1. **Use Descriptive IDs**: Each component should have a unique, descriptive ID
2. **Add Descriptions**: Include route descriptions for better visual clarity
3. **Group Related Routes**: Organize routes logically for complex multi-agent systems
4. **Parameter Documentation**: Use clear parameter names and values

## Agent ID Source Use Cases

### When to Use Different Agent ID Sources

#### **Route ID Source (`route`)**
- **Best for**: Static agent assignment, simple routing scenarios
- **Use case**: Each route is dedicated to a specific agent type
- **Example**: Separate routes for different AI models or business domains

#### **Header Source (`header`)**
- **Best for**: External systems, API endpoints, user preferences
- **Use case**: When the client/caller specifies which agent to use
- **Example**: REST API where client sends `AgentType: google` header

#### **Property Source (`property`)**
- **Best for**: Complex routing logic, intermediate processing decisions
- **Use case**: When agent selection is determined by business logic within the route
- **Example**: Routing based on message complexity, user tier, or content analysis

#### **Variable Source (`variable`)**
- **Best for**: Cross-route agent selection, shared state scenarios
- **Use case**: When agent selection needs to be shared across multiple routes or processing steps
- **Example**: Multi-step workflows where earlier routes determine agent for later processing

### Real-World Examples

#### **Customer Support Routing**
```properties
# Route high-priority tickets to advanced AI
multi.agent.id.source=property
multi.agent.id.source.property=ticket.priority

# Configure agents
multi.agent.names=basic,advanced
basic.provider.model.factory.class=io.kaoto.forage.models.chat.ollama.OllamaProvider
advanced.provider.model.factory.class=io.kaoto.forage.models.chat.google.GoogleGeminiProvider
```

#### **User Preference-Based Routing**
```properties
# Let users choose their preferred AI model
multi.agent.id.source=header
multi.agent.id.source.header=X-Preferred-Agent

# Configure agents
multi.agent.names=google,ollama,openai
```

## Advanced Multi-Agent Patterns

### Agent Chaining

Chain agents to create sophisticated workflows:

```yaml
- route:
    id: agent-chain
    from:
      uri: direct:start
      steps:
        - to:
            uri: langchain4j-agent:analysis
            parameters:
              agentFactory: "#class:io.kaoto.forage.agent.factory.MultiAgentFactory"
        - to:
            uri: langchain4j-agent:synthesis  
            parameters:
              agentFactory: "#class:io.kaoto.forage.agent.factory.MultiAgentFactory"
        - log: "Final result: ${body}"
```

### Conditional Agent Selection

Use Camel's choice component for dynamic agent selection:

```yaml
- route:
    id: conditional-agent
    from:
      uri: direct:process
      steps:
        - choice:
            when:
              - expression:
                  simple: "${header.complexity} == 'high'"
                steps:
                  - to:
                      uri: langchain4j-agent:advanced
                      parameters:
                        agentFactory: "#class:io.kaoto.forage.agent.factory.MultiAgentFactory"
            otherwise:
              steps:
                - to:
                    uri: langchain4j-agent:simple
                    parameters:
                      agentFactory: "#class:io.kaoto.forage.agent.factory.MultiAgentFactory"
```

### Memory Management

Configure different memory strategies per agent:

```properties
# Long-term memory agent
agent1.provider.features.memory.factory.class=io.kaoto.forage.memory.chat.redis.RedisChatMemoryFactory

# Short-term memory agent
agent2.provider.features.memory.factory.class=io.kaoto.forage.memory.chat.messagewindow.MessageWindowChatMemoryBeanProvider

# Stateless agent
agent3.provider.features=memoryless
```

### Guardrails

Guardrails allow you to add validation, filtering, or transformation logic that runs before (input guardrails) or after (output guardrails) an agent processes a request. Guardrails are configured as fully-qualified class names.

#### Configuring Guardrails

Configure guardrails in your `forage-agent-factory.properties` file:

```properties
# Input guardrails - executed before the agent processes input
guardrails.input.classes=com.example.ContentFilterGuardrail,com.example.RateLimitGuardrail

# Output guardrails - executed after the agent produces output
guardrails.output.classes=com.example.PiiRedactionGuardrail,com.example.ToxicityFilterGuardrail
```

#### Named Agent Guardrails

For multi-agent configurations, use prefixed guardrails for each agent:

```properties
# Define agents
multi.agent.names=secure,standard

# Secure agent with strict guardrails
secure.provider.agent.class=io.kaoto.forage.agent.simple.SimpleAgent
secure.provider.model.factory.class=io.kaoto.forage.models.chat.openai.OpenAIProvider
secure.guardrails.input.classes=com.example.StrictContentFilter,com.example.AuthorizationGuardrail
secure.guardrails.output.classes=com.example.PiiRedactionGuardrail,com.example.ComplianceGuardrail

# Standard agent with minimal guardrails
standard.provider.agent.class=io.kaoto.forage.agent.simple.SimpleAgent
standard.provider.model.factory.class=io.kaoto.forage.models.chat.ollama.OllamaProvider
standard.guardrails.output.classes=com.example.BasicOutputFilter
```

#### Environment Variable Configuration

Guardrails can also be configured via environment variables:

```bash
# Single guardrail
export GUARDRAILS_INPUT_CLASSES=com.example.ContentFilterGuardrail

# Multiple guardrails (comma-separated)
export GUARDRAILS_OUTPUT_CLASSES=com.example.PiiRedactionGuardrail,com.example.ToxicityFilterGuardrail

# Named agent guardrails (prefix with agent name in uppercase)
export SECURE_GUARDRAILS_INPUT_CLASSES=com.example.StrictContentFilter
```

## Configuration Reference

### Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `GOOGLE_API_KEY` | Google Gemini API key | `sk-...` |
| `OLLAMA_BASE_URL` | Ollama server URL | `http://localhost:11434` |
| `OLLAMA_MODEL_NAME` | Ollama model name | `llama3.1:latest` |
| `MULTI_AGENT_NAMES` | Comma-separated agent names | `google,ollama,openai` |
| `MULTI_AGENT_ID_SOURCE` | Agent ID extraction strategy | `route`, `header`, `property`, `variable` |
| `MULTI_AGENT_ID_SOURCE_HEADER` | Header name for agent ID (when using header source) | `AgentType` |
| `MULTI_AGENT_ID_SOURCE_PROPERTY` | Property name for agent ID (when using property source) | `agent.name` |
| `MULTI_AGENT_ID_SOURCE_VARIABLE` | Variable name for agent ID (when using variable source) | `selectedAgent` |
| `GUARDRAILS_INPUT_CLASSES` | Comma-separated input guardrail class names | `com.example.InputFilter` |
| `GUARDRAILS_OUTPUT_CLASSES` | Comma-separated output guardrail class names | `com.example.OutputFilter` |

### System Properties

Configure via `-D` flags:

```bash
-Dgoogle.api.key=your-key
-Dollama.base.url=http://localhost:11434
-Dmulti.agent.names=google,ollama
-Dmulti.agent.id.source=header
-Dmulti.agent.id.source.header=AgentType
-Dguardrails.input.classes=com.example.InputFilter
-Dguardrails.output.classes=com.example.OutputFilter
```

### Named Configurations

Support multiple instances with prefixes:

```properties
# Default configuration
provider.model.factory.class=io.kaoto.forage.models.chat.openai.OpenAIProvider

# Named configurations
google.provider.model.factory.class=io.kaoto.forage.models.chat.google.GoogleGeminiProvider
ollama.provider.model.factory.class=io.kaoto.forage.models.chat.ollama.OllamaProvider
```

## Troubleshooting

### Common Issues

1. **Missing Dependencies**: Ensure all required Maven dependencies are included
2. **API Key Configuration**: Verify API keys are properly set in configuration files
3. **Model Availability**: Check that configured models are available and accessible
4. **Memory Configuration**: Ensure memory providers are properly configured for stateful agents
5. **Agent ID Source Issues**:
   - **Unknown Agent Error**: Check that the extracted agent ID matches one of the configured agent names in `multi.agent.names`
   - **Header/Property/Variable Not Found**: Verify the header, property, or variable is set before reaching the agent endpoint
   - **Missing Source Configuration**: When using `header`, `property`, or `variable` sources, ensure the corresponding name configuration is set
   - **Invalid Source Type**: Check that `multi.agent.id.source` is set to one of: `route`, `header`, `property`, `variable`
6. **Guardrail Issues**:
   - **ClassNotFoundException**: Ensure guardrail class names are fully-qualified (e.g., `com.example.MyGuardrail`) and the classes are available on the classpath
   - **RuntimeForageException**: Check that guardrail classes can be loaded by the application's class loader; in Quarkus or Spring Boot, ensure classes are properly packaged
   - **Guardrails Not Executing**: Verify the configuration property names are correct (`guardrails.input.classes` or `guardrails.output.classes`) and for named agents, include the prefix (e.g., `myagent.guardrails.input.classes`)

### Debug Mode

Enable debug logging for troubleshooting:

```bash
camel run multi-agent.camel.yaml --logging-level=DEBUG
```

## Next Steps

1. **Custom Agents**: Extend the library with custom agent implementations
2. **Tool Integration**: Add custom tools for specific business logic
3. **Monitoring**: Implement monitoring and observability for multi-agent systems
4. **Scaling**: Consider distributed deployment for production workloads

For more advanced topics, see the [Contributing Beans Guide](contributing-beans.md) and [Project Structure Documentation](structure.md).