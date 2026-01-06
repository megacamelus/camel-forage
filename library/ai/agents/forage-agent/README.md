# Forage Agent

A composable AI agent implementation for Apache Camel that provides conversational chat functionality with configurable memory support.

## Overview

The `forage-agent` module provides a complete AI agent implementation (`SimpleAgent`) that can work with or without memory based on available providers and configuration. This agent is ideal for universal applications that need flexibility between memory-enabled conversations and stateless interactions.

## Features

- **Composable Design**: Works with or without memory based on configuration and available providers
- **Memory Support**: Optional conversation history using configurable chat memory providers
- **Tool Integration**: Supports Apache Camel tool providers for extended functionality
- **RAG Support**: Built-in support for Retrieval Augmented Generation (RAG)
- **Guardrails**: Configurable input and output guardrails for safety and content filtering
- **ServiceLoader Discovery**: Automatically discovered by agent factories

## Quick Start

### 1. Add Dependency

```xml
<dependency>
    <groupId>io.kaoto.forage</groupId>
    <artifactId>forage-agent</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. Use with Agent Factories

The composable agent is automatically discovered and used when you include this module:

```java
from("direct:chat")
    .setBody(constant("My name is Alice"))
    .setHeader(Headers.MEMORY_ID, constant(1))
    .to("langchain4j-agent:memory-agent?agentFactory=#class:io.kaoto.forage.agent.factory.MultiAgentFactory")
    .log("Response: ${body}");

from("timer:check?delay=5000&repeatCount=1")
         .setBody(constant("What is my name?"))
        .setHeader(Headers.MEMORY_ID, constant(1))
        .to("langchain4j-agent:test-memory-agent?agentFactory=#class:io.kaoto.forage.agent.factory.MultiAgentFactory")
        .log("${body}");
```

The second route should have a response similar to `Your name is Alice.`

### 3. Required Dependencies

For a complete setup, you'll also need:

- A chat model provider (e.g., `forage-model-openai`, `forage-model-google-gemini`)
- A chat memory provider (e.g., `forage-memory-message-window`) - optional for memory support
- Agent factories (`forage-agent-factories`)

## Configuration

The SimpleAgent is configured automatically by the agent factories using ServiceLoader discovery. Configuration includes:

- **Chat Model**: Automatically discovered from available model providers
- **Chat Memory Provider**: Automatically discovered from available memory factories (optional)
- **Retrieval Augmentor**: Optional RAG configuration
- **Guardrails**: Optional input/output filtering
- **Memory Mode**: Automatically determined based on available memory providers

## Implementation Details

### Main Class

- **`SimpleAgent`** (`io.kaoto.forage.agent.simple.SimpleAgent`)
  - Implements the `Agent` interface from camel-langchain4j-agent-api
  - Implements `ConfigurationAware` for automatic configuration
  - Delegates to `ForageAgentWithMemory` or `ForageAgentWithoutMemory` based on configuration
- **`ForageAgentWithMemory`** - Uses `AiAgentWithMemoryService` for memory-based conversations
- **`ForageAgentWithoutMemory`** - Uses `AiAgentService` for stateless interactions

### Key Features

- **Memory ID Support**: Each conversation can have a unique memory ID for isolation (when memory is available)
- **System Message Support**: Supports both user messages and system messages
- **Thread Safety**: Designed to be thread-safe for concurrent usage
- **Configurable Tools**: Integrates with Apache Camel's tool provider system
- **Automatic Fallback**: Gracefully falls back to memoryless mode when no memory provider is available

## Memory Management

When a memory provider is available, the agent:
- Stores conversation history per memory ID
- Retrieves relevant context for responses
- Maintains conversation continuity across interactions

When no memory provider is available, the agent:
- Operates in stateless mode
- Processes each interaction independently
- Provides optimal performance for single-turn interactions

## Agent Modes

| Feature | With Memory | Without Memory |
|---------|-------------|----------------|
| Conversation History | ✅ Yes | ❌ No |
| Memory ID Support | ✅ Yes | ❌ Ignored |
| Use Case | Multi-turn conversations | Single interactions |
| Resource Usage | Higher (stores history) | Lower (stateless) |
| Configuration | Memory provider required | No memory provider needed |

## Service Registration

This module registers the `SimpleAgent` via ServiceLoader in:
```
META-INF/services/org.apache.camel.component.langchain4j.agent.api.Agent
```

## Requirements

- Java 17+
- Apache Camel 4.14.0+
- LangChain4j 1.2.0+
- A compatible chat model provider
- A compatible chat memory provider

## See Also

- [Forage Agent Factories](../forage-agent-factories/README.md) - Agent factory implementations
- [Agents Overview](../README.md) - Complete agents documentation
- [Multi-Agent Development Guide](../../../docs/multi-agent-guide.md) - Multi-agent systems with Camel and Kaoto