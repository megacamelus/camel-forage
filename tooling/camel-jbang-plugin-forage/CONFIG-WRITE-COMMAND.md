# ConfigWriteCommand Architecture

## Overview

`ConfigWriteCommand` is a Camel JBang plugin command that writes Forage configuration properties from JSON input to properties files. It's designed to support the Kaoto VSCode extension by providing a machine-writable interface for configuration management.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         ConfigWriteCommand Flow                          │
└─────────────────────────────────────────────────────────────────────────┘

INPUT SOURCES
═════════════
┌──────────────┐     ┌──────────────┐
│ --input JSON │ OR  │ stdin (pipe) │
└──────┬───────┘     └──────┬───────┘
       │                    │
       └────────┬───────────┘
                │
                ▼
        ┌───────────────┐
        │  JSON Input   │
        │  {            │
        │   "forage.jdbc.url": "...",
        │   "forage.bean.name": "myPG",
        │   "kind": "postgresql"
        │  }            │
        └───────┬───────┘
                │
                ▼

COMMAND OPTIONS
═══════════════
┌─────────────────────────────────────────────────┐
│ --dir/-d      : Target directory (default: pwd) │
│ --strategy/-s : "forage" or "application"       │
│ --delete      : Delete mode flag                │
│ --name/-n     : Instance name (for delete)      │
└─────────────────────────────────────────────────┘
                │
                ▼

PROCESSING PIPELINE
═══════════════════

1. Parse JSON Input
   ┌──────────────────────────────────────┐
   │ parseJsonInput(String json)          │
   │ → Map<String, String> configMap      │
   └──────────────┬───────────────────────┘
                  │
                  ▼

2. Group by Factory Type
   ┌──────────────────────────────────────────────────────┐
   │ groupByFactory(Map<String, String> configMap)        │
   │                                                       │
   │ Extracts:                                            │
   │  • Bean name from "forage.bean.name" or "bean.name" │
   │  • Kind from "kind" or "forage.kind"                │
   │  • Factory type from property keys or kind          │
   │                                                       │
   │ Uses ForageCatalog to:                               │
   │  • Normalize property keys (strip forage. prefix)   │
   │  • Find factory type for each property              │
   │  • Build prefixed keys: forage.{name}.{property}    │
   │                                                       │
   │ Returns: Map<String, FactoryConfig>                  │
   │   FactoryConfig(beanName, properties)               │
   └──────────────┬───────────────────────────────────────┘
                  │
                  ▼

3. Determine Properties File
   ┌──────────────────────────────────────────┐
   │ getPropertiesFileName(factoryTypeKey)    │
   │                                           │
   │ Strategy "forage":                        │
   │   → forage-{factoryType}.properties      │
   │                                           │
   │ Strategy "application":                   │
   │   → application.properties               │
   └──────────────┬────────────────────────────┘
                  │
                  ▼

4. Write/Update Properties File
   ┌──────────────────────────────────────────────────────┐
   │ writePropertiesFile(file, properties, factoryType)   │
   │                                                       │
   │ If file doesn't exist:                               │
   │   • Create with header comment                       │
   │   • Write all properties                             │
   │                                                       │
   │ If file exists:                                      │
   │   • Read existing lines (preserve comments/order)    │
   │   • Update matching properties in-place              │
   │   • Append new properties at end                     │
   │   • Write back to file                               │
   └──────────────┬───────────────────────────────────────┘
                  │
                  ▼

OUTPUT
══════
┌─────────────────────────────────────────────────────────┐
│ JSON Response (stdout)                                   │
│ {                                                        │
│   "success": true,                                       │
│   "propertiesFile": "/path/to/file.properties",         │
│   "operation": "create" | "update",                      │
│   "message": "Successfully created/updated...",          │
│   "beanName": "myPG"                                     │
│ }                                                        │
└─────────────────────────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────┐
│ Properties File (filesystem)                             │
│                                                          │
│ # Forage PostgreSQL JDBC Configuration                  │
│ # Generated by Camel Forage JBang Plugin                │
│                                                          │
│ forage.myPG.jdbc.db.kind=postgresql                     │
│ forage.myPG.jdbc.url=jdbc:postgresql://localhost:5432/ │
│ forage.myPG.jdbc.username=test                          │
│ forage.myPG.jdbc.password=test                          │
│ forage.myPG.jdbc.transaction.enabled=true               │
└─────────────────────────────────────────────────────────┘


DELETE MODE
═══════════
┌──────────────────────────────────────────────────────────┐
│ handleDelete()                                            │
│                                                           │
│ 1. Requires --name parameter                             │
│ 2. Scans properties files based on strategy              │
│ 3. Removes lines matching: forage.{instanceName}.*       │
│ 4. Collects deleted types (e.g., "ollama", "postgresql") │
│ 5. Calls cleanupUnusedDependencies()                     │
│ 6. Preserves comments and other properties               │
│                                                           │
│ Returns JSON with deletion and dependency cleanup results│
└──────────────────────────────────────────────────────────┘
                  │
                  ▼
┌──────────────────────────────────────────────────────────┐
│ cleanupUnusedDependencies()                               │
│                                                           │
│ 1. Scans remaining properties for configured types       │
│ 2. For each deleted type not in remaining:               │
│    • Remove bean GAV (e.g., forage-jdbc-postgresql)      │
│ 3. For each factory type with no remaining instances:    │
│    • Remove factory variant GAVs (base, springboot,      │
│      quarkus)                                            │
│ 4. Updates camel.jbang.dependencies* in                  │
│    application.properties                                │
└──────────────────────────────────────────────────────────┘
```

## Key Components

### ForageCatalog (Singleton)
- Metadata about all factory types
- Property key normalization
- Factory type detection from properties
- Properties file name mapping
- Bean GAV lookup by bean name
- Factory variant GAV lookup

### FactoryConfig (Record)
- `beanName`: String (optional prefix for properties)
- `kind`: String (bean type, e.g., "postgresql", "ollama")
- `properties`: Map<String, String> (key-value pairs)

### DependencyInfo (Record)
- `baseDependencies`: Set<String> (bean-specific GAVs)
- `mainDependencies`: Set<String> (base factory GAVs)
- `springBootDependencies`: Set<String> (Spring Boot factory GAVs)
- `quarkusDependencies`: Set<String> (Quarkus factory GAVs)

### DeletedConfigInfo (Record)
- `deletedPropertyKeys`: Set<String> (all removed property keys)
- `deletedTypes`: Set<String> (type segments from deleted keys, e.g., "jdbc", "ollama")
- `deletedFactoryTypeKeys`: Set<String> (factory type keys that were deleted)

## Usage Examples

### Example 1: PostgreSQL with All Features

**Input JSON:**
```json
{
  "forage.jdbc.db.kind": "postgresql",
  "forage.jdbc.url": "jdbc:postgresql://localhost:5432/",
  "forage.jdbc.username": "test",
  "forage.jdbc.password": "test",
  "forage.jdbc.transaction.enabled": "true",
  "forage.jdbc.aggregation.repository.enabled": "true",
  "forage.jdbc.idempotent.repository.enabled": "true",
  "kind": "postgresql",
  "forage.bean.name": "myPG"
}
```

**Command:**
```bash
camel forage config write --input '{"forage.jdbc.db.kind":"postgresql",...}' --strategy application
```

**Output (application.properties):**
```properties
forage.myPG.jdbc.db.kind=postgresql
forage.myPG.jdbc.url=jdbc:postgresql://localhost:5432/
forage.myPG.jdbc.username=test
forage.myPG.jdbc.password=test
forage.myPG.jdbc.transaction.enabled=true
forage.myPG.jdbc.aggregation.repository.enabled=true
forage.myPG.jdbc.idempotent.repository.enabled=true
```

### Example 2: Infinispan Chat Memory

**Input JSON:**
```json
{
  "forage.infinispan.server-list": "localhost:11223",
  "kind": "infinispan",
  "forage.bean.name": "myMemory"
}
```

**Output (application.properties):**
```properties
forage.myMemory.infinispan.server-list=localhost:11223
```

### Example 3: Multiple Configs to Same File

First call writes PostgreSQL config → creates file  
Second call writes Infinispan config → appends to same file

**Result:** Both configurations coexist in application.properties

### Example 4: Delete Operation (Single Bean Type)

**Scenario:** PostgreSQL and MariaDB are both configured. Delete PostgreSQL only.

**Before (application.properties):**
```properties
forage.myPG.jdbc.url=jdbc:postgresql://localhost:5432/
forage.myPG.jdbc.username=test
forage.myMariaDB.jdbc.url=jdbc:mariadb://localhost:3306/
forage.myMariaDB.jdbc.username=test
camel.jbang.dependencies=org.apache.camel.forage:forage-jdbc-postgresql:1.0-SNAPSHOT,org.apache.camel.forage:forage-jdbc-mariadb:1.0-SNAPSHOT
camel.jbang.dependencies.main=org.apache.camel.forage:forage-jdbc:1.0-SNAPSHOT
```

**Command:**
```bash
camel forage config write --delete --name myPG
```

**After (application.properties):**
```properties
forage.myMariaDB.jdbc.url=jdbc:mariadb://localhost:3306/
forage.myMariaDB.jdbc.username=test
camel.jbang.dependencies=org.apache.camel.forage:forage-jdbc-mariadb:1.0-SNAPSHOT
camel.jbang.dependencies.main=org.apache.camel.forage:forage-jdbc:1.0-SNAPSHOT
```

**Effect:**
- Removes all `forage.myPG.*` properties
- Removes `forage-jdbc-postgresql` from dependencies (no longer needed)
- Keeps `forage-jdbc` factory dependency (still used by MariaDB)
- Preserves MariaDB configuration

### Example 5: Delete Operation (Last Bean of Factory Type)

**Scenario:** Only Ollama is configured. Delete it entirely.

**Before (application.properties):**
```properties
forage.myOllama.ollama.model.name=llama3
forage.myOllama.ollama.base.url=http://localhost:11434
camel.jbang.dependencies=org.apache.camel.forage:forage-model-ollama:1.0-SNAPSHOT
camel.jbang.dependencies.main=org.apache.camel.forage:forage-agent:1.0-SNAPSHOT
camel.jbang.dependencies.spring-boot=org.apache.camel.forage:forage-agent-starter:1.0-SNAPSHOT
camel.jbang.dependencies.quarkus=org.apache.camel.forage:forage-quarkus-agent-deployment:1.0-SNAPSHOT
```

**Command:**
```bash
camel forage config write --delete --name myOllama
```

**After (application.properties):**
```properties
# Empty - all forage properties and dependencies removed
```

**Effect:**
- Removes all `forage.myOllama.*` properties
- Removes `forage-model-ollama` bean dependency
- Removes all agent factory dependencies (no other agent beans configured)

## Command Options

| Option | Short | Description | Default |
|--------|-------|-------------|---------|
| `--input` | `-i` | JSON input string containing configuration values | stdin |
| `--dir` | `-d` | Directory where properties files will be written | current directory |
| `--strategy` | `-s` | Property file strategy: 'forage' or 'application' | forage |
| `--delete` | | Delete configuration for a specific instance | false |
| `--name` | `-n` | Instance name to delete (used with --delete) | |

## Property Key Transformation

The command transforms input keys to prefixed output keys:

| Input Key | Bean Name | Output Key |
|-----------|-----------|------------|
| `forage.jdbc.url` | `myPG` | `forage.myPG.jdbc.url` |
| `jdbc.url` | `myPG` | `forage.myPG.jdbc.url` |
| `forage.jms.broker.url` | `cf1` | `forage.cf1.jms.broker.url` |
| `ollama.model.name` | `test` | `forage.test.ollama.model.name` |

## File Update Behavior

When updating existing properties files:
1. Preserves comments and blank lines
2. Updates matching properties in-place
3. Appends new properties at the end
4. Maintains original file order

## Dependency Management

The command automatically manages `camel.jbang.dependencies*` properties in `application.properties` based on the factory type and kind being configured.

### How It Works

When writing configuration, the command:
1. Looks up the bean GAV from the catalog (e.g., `forage-jdbc-postgresql` for kind `postgresql`)
2. Looks up factory variant GAVs for each runtime (base, springboot, quarkus)
3. Merges these dependencies with any existing dependencies in `application.properties`

### Dependency Properties

| Property | Description | Example |
|----------|-------------|---------|
| `camel.jbang.dependencies` | Bean-specific dependencies (database driver, broker client) | `org.apache.camel.forage:forage-jdbc-postgresql:1.0-SNAPSHOT` |
| `camel.jbang.dependencies.main` | Base/main runtime factory dependencies | `org.apache.camel.forage:forage-jdbc:1.0-SNAPSHOT` |
| `camel.jbang.dependencies.spring-boot` | Spring Boot runtime dependencies | `org.apache.camel.forage:forage-jdbc-starter:1.0-SNAPSHOT` |
| `camel.jbang.dependencies.quarkus` | Quarkus runtime dependencies | `org.apache.camel.forage:forage-quarkus-jdbc-deployment:1.0-SNAPSHOT` |

### Example

**Input JSON:**
```json
{
  "forage.jdbc.db.kind": "postgresql",
  "forage.jdbc.url": "jdbc:postgresql://localhost:5432/",
  "forage.jdbc.username": "test",
  "forage.jdbc.password": "test",
  "kind": "postgresql",
  "forage.bean.name": "myPG"
}
```

**Generated application.properties:**
```properties
# Forage configuration properties
forage.myPG.jdbc.db.kind=postgresql
forage.myPG.jdbc.url=jdbc:postgresql://localhost:5432/
forage.myPG.jdbc.username=test
forage.myPG.jdbc.password=test

# Automatically generated dependencies
camel.jbang.dependencies=org.apache.camel.forage:forage-jdbc-postgresql:1.0-SNAPSHOT
camel.jbang.dependencies.main=org.apache.camel.forage:forage-jdbc:1.0-SNAPSHOT
camel.jbang.dependencies.spring-boot=org.apache.camel.forage:forage-jdbc-starter:1.0-SNAPSHOT
camel.jbang.dependencies.quarkus=org.apache.camel.forage:forage-quarkus-jdbc-deployment:1.0-SNAPSHOT
```

### Preserving Existing Dependencies

When updating dependencies, the command:
- **Never removes** existing dependencies from any `camel.jbang.dependencies*` property
- **Only adds** new forage dependencies to the list
- **Preserves order** of existing dependencies

**Example with existing dependencies:**

Before:
```properties
camel.jbang.dependencies=com.example:my-custom-lib:1.0
```

After adding PostgreSQL JDBC configuration:
```properties
camel.jbang.dependencies=com.example:my-custom-lib:1.0,org.apache.camel.forage:forage-jdbc-postgresql:1.0-SNAPSHOT
```

### Dependency Cleanup During Deletion

When deleting a configuration with `--delete --name <instanceName>`, the command intelligently cleans up unused dependencies:

#### Cleanup Logic

1. **Delete Properties**: Remove all properties matching `forage.{instanceName}.*`
2. **Collect Types**: Extract type segments from deleted keys (e.g., `forage.myPG.jdbc.url` → type `jdbc`)
3. **Scan Remaining**: Find all types still configured in remaining properties
4. **Determine Unused**:
   - If a deleted type has no remaining instances → remove its bean GAV
   - If a factory type has no remaining beans → remove all factory variant GAVs

#### What Gets Removed

| Condition | Dependencies Removed |
|-----------|---------------------|
| Bean type no longer configured (e.g., no more `postgresql` instances) | Bean GAV (`forage-jdbc-postgresql`) |
| Factory type no longer used (e.g., no more `jdbc.*` properties) | Factory variant GAVs (`forage-jdbc`, `forage-jdbc-starter`, `forage-quarkus-jdbc-deployment`) |

#### What Is Preserved

- Dependencies for other bean types still configured
- Factory dependencies when other beans of the same factory type exist
- Non-forage dependencies (custom user dependencies)
- Comments and file structure

#### Delete Output Format

```json
{
  "success": true,
  "operation": "delete",
  "instanceName": "myPG",
  "results": {
    "application.properties": {
      "success": true,
      "propertiesFile": "/path/to/application.properties",
      "deletedProperties": 5,
      "message": "Deleted configuration for instance 'myPG'"
    },
    "dependencyCleanup": {
      "removedDependencies": [
        "org.apache.camel.forage:forage-jdbc-postgresql:1.0-SNAPSHOT"
      ],
      "count": 1
    }
  }
}
```

## Error Handling

The command returns exit code 1 and JSON error response for:
- Empty or invalid JSON input
- Missing required configuration
- File system errors
- Invalid directory paths

**Error Response Format:**
```json
{
  "success": false,
  "error": "Error message description"
}
```
