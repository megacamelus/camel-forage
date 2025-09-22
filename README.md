# Camel Forage

An opinionated library of beans for Apache Camel that provides pre-configured components for AI integrations and other bean-based components.

## Overview

Camel Forage simplifies the configuration of Apache Camel components by providing ready-to-use beans that follow best practices. The library is particularly focused on AI components, offering seamless integration with various AI models, chat memory providers, and agent factories.

## Features

- **Plug-and-play AI components** - Pre-configured beans for AI models, agents, and memory providers
- **Multiple AI provider support** - Support for OpenAI, Google Gemini, Ollama, and extensible for other providers
- **Chat memory management** - Built-in message window chat memory with persistent storage
- **Agent factory patterns** - Default agent factory with ServiceLoader-based discovery
- **Modular architecture** - Pick only the modules you need

## Quick Start

### 1. Add Dependencies

Add the desired modules to your project. For example, to use the default agent factory with OpenAI:

```xml
<!--This component provides support for OpenAI models (GPT-3.5, GPT-4, etc.)-->
<dependency>
    <groupId>org.apache.camel.forage</groupId>
    <artifactId>camel-forage-model-open-ai</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<!--This component provides support for the message window chat memory -->
<dependency>
    <groupId>org.apache.camel.forage</groupId>
    <artifactId>forage-memory-message-window</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<!--This component adds agent factories for single and multi-agent systems -->
<dependency>
    <groupId>org.apache.camel.forage</groupId>
    <artifactId>forage-agent-factories</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<!--This component adds the composable agent implementation -->
<dependency>
    <groupId>org.apache.camel.forage</groupId>
    <artifactId>forage-agent</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. Use in Camel Routes

Simply reference the bean class in your Camel route:

```java
from("direct:start")
    .to("langchain4j-agent:test-memory-agent?agentFactory=#class:org.apache.camel.forage.agent.factory.DefaultAgentFactory");
```

The `org.apache.camel.forage.agent.factory.DefaultAgentFactory` class is a default factory that builds AI agent automagically,
based on the dependencies available on the classpath.

## Available Modules

### Core Modules

- **forage-core-ai** - Core interfaces and abstractions for AI components
- **forage-core-agent** - Agent interfaces and base classes

### AI Modules

#### Agents
- **forage-agent-factories** - Agent factories for single and multi-agent systems ([Documentation](library/ai/agents/forage-agent-factories/README.md))
- **forage-agent** - Composable agent implementation with optional memory support ([Documentation](library/ai/agents/forage-agent/README.md))

📋 **[Complete Agents Documentation](library/ai/agents/README.md)** - Comprehensive guide to all agent components

#### Models
- **camel-forage-model-open-ai** - OpenAI chat model provider ([Configuration Guide](library/ai/models/chat/forage-model-open-ai/README.md))
- **forage-model-google-gemini** - Google Gemini chat model provider ([Configuration Guide](library/ai/models/chat/forage-model-google-gemini/README.md))
- **forage-model-ollama** - Ollama chat model provider ([Configuration Guide](library/ai/models/chat/forage-model-ollama/README.md))

#### Chat Memory
- **forage-memory-message-window** - Message window chat memory with persistent storage

#### Vector Databases
- **vector-dbs** - Vector database integrations (coming soon)

#### Embeddings
- **embeddings** - Embedding model providers (coming soon)

### JDBC Modules

Camel Forage provides JDBC data source factories that simplify database connectivity with pre-configured, pooled data sources for various database systems.

#### Available JDBC Modules

- **forage-jdbc** - Core pooled JDBC functionality
- **forage-jdbc-factories** - Data source factory implementations
- **forage-jdbc-postgres** - PostgreSQL data source provider
- **forage-jdbc-mysql** - MySQL data source provider  
- **forage-jdbc-mariadb** - MariaDB data source provider
- **forage-jdbc-oracle** - Oracle data source provider
- **forage-jdbc-mssql** - Microsoft SQL Server data source provider
- **forage-jdbc-db2** - IBM DB2 data source provider
- **forage-jdbc-h2** - H2 database data source provider
- **forage-jdbc-hsqldb** - HSQLDB data source provider

#### JDBC Configuration Properties

All JDBC modules support the following configuration properties with a flexible precedence hierarchy (environment variables → system properties → configuration files → defaults):

**Database Connection:**
- `jdbc.url` - JDBC connection URL (required)
- `jdbc.username` - Database username (required)
- `jdbc.password` - Database password (required)

**Connection Pool Settings:**
- `jdbc.pool.initial.size` - Initial pool size (default: 5)
- `jdbc.pool.min.size` - Minimum pool size (default: 2)
- `jdbc.pool.max.size` - Maximum pool size (default: 20)
- `jdbc.pool.acquisition.timeout.seconds` - Connection acquisition timeout (default: 5)
- `jdbc.pool.validation.timeout.seconds` - Connection validation timeout (default: 3)
- `jdbc.pool.leak.timeout.minutes` - Connection leak detection timeout (default: 10)
- `jdbc.pool.idle.validation.timeout.minutes` - Idle connection validation timeout (default: 3)

**Transaction Settings:**
- `jdbc.transaction.timeout.seconds` - Transaction timeout (default: 30)

**Provider Configuration:**
- `provider.datasource.class` - DataSource implementation class (auto-detected based on dependencies)

#### Quick Start with PostgreSQL

1. **Start PostgreSQL database:**
```bash
camel infra run postgres
```

2. **Add dependencies to your project:**
```xml
<dependency>
    <groupId>org.apache.camel.forage</groupId>
    <artifactId>forage-jdbc-factories</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.apache.camel.forage</groupId>
    <artifactId>forage-jdbc-postgres</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

3. **Create a Camel route:**
```java
import org.apache.camel.builder.RouteBuilder;

public class Test extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:java?period=1000")
                .to("sql:select * from acme?dataSourceFactory=#class:org.apache.camel.forage.jdbc.factory.DefaultDataSourceFactory")
                .log("${body}");
    }
}
```

4. **Run with JBang:**
```bash
camel run Test.java \
  --dep=mvn:org.apache.camel.forage:forage-jdbc-factories:1.0-SNAPSHOT \
  --dep=mvn:org.apache.camel.forage:forage-jdbc-postgres:1.0-SNAPSHOT
```

This provides a fully configured, pooled data source out-of-the-box with sensible defaults.

#### Configuration Examples

**Environment Variables:**
```bash
export JDBC_URL="jdbc:postgresql://localhost:5432/mydb"
export JDBC_USERNAME="myuser"
export JDBC_PASSWORD="mypassword"
export JDBC_POOL_MAX_SIZE="50"
```

**System Properties:**
```bash
-Djdbc.url=jdbc:postgresql://localhost:5432/mydb
-Djdbc.username=myuser
-Djdbc.password=mypassword
-Djdbc.pool.max.size=50
```

**Configuration File (forage-datasource-factory.properties):**
```properties
jdbc.url=jdbc:postgresql://localhost:5432/mydb
jdbc.username=myuser
jdbc.password=mypassword
jdbc.pool.max.size=50
```

## Configuration

Camel Forage uses a flexible configuration system that supports multiple sources with a defined precedence hierarchy:

1. **Environment variables** (highest precedence)
2. **System properties** 
3. **Configuration files** (e.g., `forage-model-*.properties`)
4. **Default values** (where applicable)

### Model Configuration

Each AI model provider has its own configuration requirements and options. For detailed configuration instructions, including environment variables, system properties, and configuration files, please refer to the respective model documentation:

- **OpenAI**: See [OpenAI Configuration Guide](library/ai/models/chat/forage-model-open-ai/README.md)
- **Google Gemini**: See [Google Gemini Configuration Guide](library/ai/models/chat/forage-model-google-gemini/README.md)  
- **Ollama**: See [Ollama Configuration Guide](library/ai/models/chat/forage-model-ollama/README.md)

### Quick Configuration Examples

For immediate setup, here are minimal configuration examples:

#### OpenAI
```bash
export OPENAI_API_KEY="sk-your-api-key-here"
export OPENAI_MODEL_NAME="gpt-4"  # Optional, defaults to gpt-3.5-turbo
```

#### Google Gemini
```bash
export GOOGLE_API_KEY="your-google-api-key"
export GOOGLE_MODEL_NAME="gemini-pro"
```

#### Ollama
```bash
export OLLAMA_BASE_URL="http://localhost:11434"  # Optional, this is the default
export OLLAMA_MODEL_NAME="llama3"                # Optional, this is the default
```

## Architecture

Camel Forage uses a ServiceLoader-based discovery mechanism to automatically wire components together:

1. **ModelProvider** - Provides chat models (Ollama, Google Gemini, etc.)
2. **ChatMemoryFactory** - Creates chat memory providers for conversation persistence
3. **AgentFactory** - Orchestrates the creation of agents with models and memory
4. **Agent** - The actual agent implementation

The `DefaultAgentFactory` automatically discovers and combines these components using Java's ServiceLoader mechanism.

## Examples

### Basic AI Agent Route

Example for a memory-less agent.

```java
from("timer:ai?period=30000")
    .setBody(constant("Tell me a joke"))
    .to("langchain4j-agent:joke-agent?agentFactory=#class:org.apache.camel.forage.agent.factory.DefaultAgentFactory")
    .log("AI Response: ${body}");
```

### Custom Configuration

```java
public class MyRoutes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:chat")
            .to("langchain4j-agent:chat-agent?agentFactory=#class:org.apache.camel.forage.agent.factory.DefaultAgentFactory")
            .log("Chat response: ${body}");
    }
}
```

## Requirements

- Java 17+
- Apache Camel 4.14.0+
- LangChain4j 1.2.0+

## Contributing

This project follows standard Maven conventions. To build:

```bash
mvn clean install
```

### Integrating Apache Camel Components

If you're developing Apache Camel components and want to integrate them with the Camel Forage library, or if you want to create new providers for AI models, vector databases, or other services, please refer to our comprehensive [Contributing Beans Guide](docs/contributing-beans.md).

This guide is essential reading for:
- **Component developers** who want to add factory-based configuration to their Apache Camel components
- **Library contributors** who want to create new AI model providers, vector database integrations, or other service providers
- **Maintainers** who need to understand the architecture and patterns used throughout the Forage library

## License

This project is licensed under the Apache License 2.0.