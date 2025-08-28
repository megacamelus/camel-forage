# Camel Forage Chat Memory TCK

This module provides a Technology Compatibility Kit (TCK) for testing chat memory implementations in the Camel Forage framework.

## Overview

The TCK contains a comprehensive test suite that validates the core functionality of chat memory implementations:

- **ChatMemoryFactoryTCK**: Tests for `ChatMemoryFactory` implementations that cover the entire chat memory workflow

## Usage

### Testing a ChatMemoryFactory Implementation

To test your `ChatMemoryFactory` implementation, extend the `ChatMemoryFactoryTCK` class:

```java
public class MyMemoryFactoryTest extends ChatMemoryFactoryTCK {
    @Override
    protected ChatMemoryFactory createChatMemoryFactory() {
        return new MyMemoryFactory();
    }
}
```

## Maven Dependency

Add the TCK as a test dependency in your memory implementation module:

```xml
<dependency>
    <groupId>org.apache.camel.forage</groupId>
    <artifactId>forage-memory-tck</artifactId>
    <version>${project.version}</version>
    <scope>test</scope>
</dependency>
```

## Test Coverage

### ChatMemoryFactoryTCK

- Factory instantiation without exceptions
- ChatMemoryProvider creation
- Multiple provider creation
- ChatMemory creation from provider
- Message storage and retrieval
- Memory isolation between different IDs
- Memory persistence for same ID
- Empty memory handling
- Memory clearing
- Multiple message type support

## Included Test Implementations

The TCK module includes concrete test implementations for all Camel Forage memory providers:

### MessageWindowChatMemoryTCKTest
Tests the `MessageWindowChatMemoryFactory` implementation using in-memory persistence.

### RedisMemoryTCKTest  
Tests the `RedisMemoryFactory` implementation using Redis with testcontainers. This test:
- Automatically starts a Redis container using testcontainers
- Configures the Redis connection for testing
- Validates all memory operations against real Redis storage
- Cleans up Redis configuration after tests

## Test Execution

To run all memory implementation tests:
```bash
mvn test -pl library/ai/chat-memory/forage-memory-tck
```

To run specific implementation tests:
```bash
# Test MessageWindow implementation only
mvn test -pl library/ai/chat-memory/forage-memory-tck -Dtest=MessageWindowChatMemoryTCKTest

# Test Redis implementation only  
mvn test -pl library/ai/chat-memory/forage-memory-tck -Dtest=RedisMemoryTCKTest
```

## Test Coverage

When run successfully, the TCK validates that implementations correctly handle:

- Message persistence and retrieval
- Memory isolation between conversation IDs
- ChatMemoryProvider and ChatMemoryFactory lifecycles
- Error handling and edge cases
- Message ordering and content preservation
- Empty memory states and clearing operations

All tests use AssertJ for fluent assertions, JUnit 5 for test execution, and testcontainers for Redis integration testing.