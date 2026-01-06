# Forage Agent Factories

Agent factory implementations for Apache Camel that provide automatic discovery and configuration of AI agents using ServiceLoader, including support for multi-agent orchestration.

## Overview

The `forage-agent-factories` module provides the `MultiAgentFactory` implementation for AI agent orchestration. This factory serves as the main entry point for creating AI agents in the Forage ecosystem, automatically discovering and wiring together agents, model providers, and chat memory factories using Java's ServiceLoader mechanism. It supports both single-agent and multi-agent configurations.

## Features

- **ServiceLoader Discovery**: Automatically discovers agents, model providers, and memory factories
- **Configuration Management**: Handles automatic configuration of discovered components
- **Singleton Pattern**: Ensures only one agent instance is created per factory
- **Multi-Agent Support**: Coordinated multi-agent systems with `MultiAgentFactory`
- **Flexible Architecture**: Works with any compatible agent implementation
- **Zero Configuration**: Works out-of-the-box with included modules
- **Agent Selectors**: Configurable strategies for multi-agent coordination

## Quick Start

### 1. Add Dependency

```xml
<dependency>
    <groupId>io.kaoto.forage</groupId>
    <artifactId>forage-agent-factories</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. Use in Camel Routes

```java
from("direct:chat")
    .to("langchain4j-agent:my-agent?agentFactory=#class:io.kaoto.forage.agent.factory.MultiAgentFactory")
    .log("Response: ${body}");
```

### 3. Complete Setup Example

For a working setup, include these dependencies:

```xml
<!-- Agent factories (this module) -->
<dependency>
    <groupId>io.kaoto.forage</groupId>
    <artifactId>forage-agent-factories</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

<!-- Agent implementation -->
<dependency>
    <groupId>io.kaoto.forage</groupId>
    <artifactId>forage-agent</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

<!-- Model provider -->
<dependency>
    <groupId>io.kaoto.forage</groupId>
    <artifactId>forage-model-openai</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

<!-- Memory provider (optional) -->
<dependency>
    <groupId>io.kaoto.forage</groupId>
    <artifactId>forage-memory-message-window</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## How It Works

The agent factories use ServiceLoader to discover and configure components:

### 1. Discovery Phase
- **Agents**: Discovers implementations of `org.apache.camel.component.langchain4j.agent.api.Agent`
- **Model Providers**: Discovers implementations of `io.kaoto.forage.core.ai.ModelProvider`
- **Memory Factories**: Discovers implementations of `io.kaoto.forage.core.ai.ChatMemoryBeanProvider`

### 2. Configuration Phase
- Creates a `ModelProvider` instance for chat model functionality
- Creates a `ChatMemoryFactory` instance for conversation memory (optional)
- Builds an `AgentConfiguration` with the discovered components
- Configures the agent if it implements `ConfigurationAware`

### 3. Agent Creation
- Returns a configured, ready-to-use agent instance
- Uses singleton pattern to ensure one agent per factory instance

## Implementation Details

### Main Classes

- **`MultiAgentFactory`** (`io.kaoto.forage.agent.factory.MultiAgentFactory`)
  - Implements `AgentFactory` from camel-langchain4j-agent-api
  - Supports both single-agent and multi-agent configurations
  - Orchestrates agents for collaborative tasks
  - Supports configurable agent ID sources and coordination
  - Thread-safe implementation with singleton agent instances per configuration

- **`ConfigurationAware`** (`io.kaoto.forage.agent.factory.ConfigurationAware`)
  - Interface for agents that accept configuration
  - Allows automatic configuration of discovered agents

- **Agent Selectors** - Various strategies for multi-agent coordination:
  - `RouteIdAgentSelector` - Uses Camel route ID
  - `HeaderAgentSelector` - Uses message header value
  - `PropertyAgentSelector` - Uses exchange property value
  - `VariableAgentSelector` - Uses exchange variable value

### ServiceLoader Integration

The factory discovers components through standard ServiceLoader files:
- `META-INF/services/org.apache.camel.component.langchain4j.agent.api.Agent`
- `META-INF/services/io.kaoto.forage.core.ai.ModelProvider`
- `META-INF/services/io.kaoto.forage.core.ai.ChatMemoryBeanProvider`

## Configuration

The factories automatically configure agents with:

- **Chat Model**: First discovered model provider
- **Chat Memory Provider**: First discovered memory factory (optional)
- **Retrieval Augmentor**: Configured through the agent if supported
- **Guardrails**: Configured through the agent if supported
- **Multi-Agent Coordination**: Agent selectors and orchestration (MultiAgentFactory)

## Error Handling

The factories handle missing components gracefully:

- **No Agent**: Throws `IllegalStateException`
- **No Model Provider**: Throws `IllegalStateException`
- **No Memory Factory**: Continues without memory (for memoryless mode)
- **Invalid Agent ID**: Throws `UndefinedAgentException` (MultiAgentFactory)

## Extending the Factories

### Creating Custom Agents

To create a custom agent that works with the factories:

1. Implement the `Agent` interface
2. Optionally implement `ConfigurationAware` for automatic configuration
3. Register via ServiceLoader in `META-INF/services/org.apache.camel.component.langchain4j.agent.api.Agent`

Example:
```java
public class MyAgent implements Agent, ConfigurationAware {
    private AgentConfiguration configuration;
    
    @Override
    public void configure(AgentConfiguration configuration) {
        this.configuration = configuration;
    }
    
    @Override
    public String chat(AiAgentBody body, ToolProvider toolProvider) {
        // Implementation
    }
}
```

### Creating Custom Model Providers

1. Implement the `ModelProvider` interface
2. Register via ServiceLoader in `META-INF/services/io.kaoto.forage.core.ai.ModelProvider`

### Creating Custom Memory Factories

1. Implement the `ChatMemoryFactory` interface
2. Register via ServiceLoader in `META-INF/services/io.kaoto.forage.core.ai.ChatMemoryBeanProvider`

## Thread Safety

`MultiAgentFactory` is thread-safe and uses proper synchronization for agent creation. The singleton pattern ensures that multiple calls to `createAgent()` return the same configured instance for each agent configuration.

## Requirements

- Java 17+
- Apache Camel 4.14.0+
- At least one compatible agent implementation
- At least one compatible model provider


## See Also

- [Forage Agent](../forage-agent/README.md) - Composable agent implementation
- [Agents Overview](../README.md) - Complete agents documentation
- [Multi-Agent Development Guide](../../../docs/multi-agent-guide.md) - Multi-agent systems with Camel and Kaoto
- [Contributing Beans Guide](../../../../docs/contributing-beans.md) - How to create custom components