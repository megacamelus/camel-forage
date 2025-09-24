# Camel Forage JBang Plugin

A JBang plugin for Apache Camel that provides utilities for working with Camel Forage components, including datasource connection testing and configuration validation.

## Installation

Install the plugin using the following command:

```bash
camel plugin add forage \
  --command='forage' \
  --description='Forage Camel JBang Plugin' \
  --artifactId='camel-jbang-plugin-forage' \
  --groupId='org.apache.camel.forage' \
  --version='1.0-SNAPSHOT' \
  --gav='org.apache.camel.forage:camel-jbang-plugin-forage:1.0-SNAPSHOT'
```

## Usage

After installation, you can use the plugin with:

```bash
camel forage [command] [options]
```

For help with available commands:

```bash
camel forage --help
```

## Uninstallation

Remove the plugin using either method:

**Option 1: Using CLI**
```bash
camel plugin delete forage
```

**Option 2: Manual removal**
Remove the plugin entry from `~/.camel-jbang-plugins.json`

## Commands

### Datasource Commands

The plugin provides utilities for testing and validating datasource connections configured through Camel Forage.

#### `datasource test-connection`

Tests a datasource connection using a `forage-datasource-factory.properties` configuration file.

**Syntax:**
```bash
camel forage datasource test-connection [datasource-name]
```

**Parameters:**
- `datasource-name` (optional): Name of the specific datasource to test. Required when multiple datasources are configured.

**How it works:**

1. **Configuration Parsing**: Reads the `forage-datasource-factory.properties` file
2. **Dynamic Dependency Loading**: Downloads the appropriate JDBC Forage module at runtime based on the `jdbc.db.kind` property
   - Example: `jdbc.db.kind=postgres` → downloads `forage-jdbc-postgres`
3. **Provider Instantiation**: Creates via reflection a `DataSourceProvider` instance (e.g., `PostgresJdbc`)
4. **Connection Testing**: Establishes connection and executes validation queries

**Examples:**

**Single Datasource Configuration:**

When only one datasource is configured in your properties file:

```bash
$ camel forage datasource test-connection
```

Output:
```
Testing connection for database: postgres
Loading JDBC dependencies for: postgres
Downloading dependency: org.apache.camel.forage:forage-jdbc-postgres:1.0-SNAPSHOT
Dependencies loaded successfully
Establishing database connection...
Database connection established successfully

Connection Details:
Database: PostgreSQL
Version: 17.5
Driver: PostgreSQL JDBC Driver
URL: jdbc:postgresql://localhost:5432/postgres
User: test

Executing validation query: SELECT version(), current_database(), current_user
Validation query executed successfully
Result: PostgreSQL 17.5 on aarch64-unknown-linux-musl, compiled by gcc (Alpine 14.2.0) 14.2.0, 64-bit | postgres | test

Validating connection health...
Connection is valid and responsive

Database connection test completed successfully!
```

**Multiple Datasource Configuration:**

When multiple datasources are configured, specify the datasource name:

```bash
$ camel forage datasource test-connection ds2
```

Output:
```
Testing connection for database: mysql
Using configuration: ds2
Loading JDBC dependencies for: mysql
Downloading dependency: org.apache.camel.forage:forage-jdbc-mysql:1.0-SNAPSHOT
Dependencies loaded successfully
Establishing database connection...
Database connection established successfully

Connection Details:
Database: MySQL
Version: 9.4.0
Driver: MySQL Connector/J
URL: jdbc:mysql://localhost:3306/test
User: root@192.168.215.1

Executing validation query: SELECT VERSION(), DATABASE(), USER()
Validation query executed successfully
Result: 9.4.0 | test | root@192.168.215.1

Validating connection health...
Connection is valid and responsive

Database connection test completed successfully!
```