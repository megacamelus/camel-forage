/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.forage.plugin.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Result for database connection test operations.
 *
 * <p>Success response example:
 * <pre>{@code
 * {
 *   "success": true,
 *   "message": "Database connection test completed successfully",
 *   "connection": {
 *     "database": "PostgreSQL",
 *     "version": "17.5",
 *     "driver": "PostgreSQL JDBC Driver",
 *     "url": "jdbc:postgresql://localhost:5432/",
 *     "user": "test",
 *     "valid": true
 *   },
 *   "validation": {
 *     "query": "SELECT version(), current_database(), current_user",
 *     "result": "PostgreSQL 17.5 on aarch64-unknown-linux-musl..."
 *   }
 * }
 * }</pre>
 *
 * <p>Error response example:
 * <pre>{@code
 * {
 *   "success": false,
 *   "error": "Connection refused"
 * }
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"success", "message", "connection", "validation", "error"})
public class ConnectionTestResult extends CommandResult {

    @JsonProperty("connection")
    private ConnectionInfo connection;

    @JsonProperty("validation")
    private ValidationInfo validation;

    private ConnectionTestResult(boolean success) {
        super(success);
    }

    /**
     * Creates a successful connection test result.
     */
    public static ConnectionTestResult success() {
        ConnectionTestResult result = new ConnectionTestResult(true);
        result.setMessage("Database connection test completed successfully");
        return result;
    }

    /**
     * Creates a failed connection test result.
     */
    public static ConnectionTestResult failure(String errorMessage) {
        ConnectionTestResult result = new ConnectionTestResult(false);
        result.setErrorMessage(errorMessage);
        return result;
    }

    public ConnectionTestResult withConnection(ConnectionInfo connection) {
        this.connection = connection;
        return this;
    }

    public ConnectionTestResult withValidation(ValidationInfo validation) {
        this.validation = validation;
        return this;
    }

    public ConnectionInfo getConnection() {
        return connection;
    }

    public ValidationInfo getValidation() {
        return validation;
    }

    /**
     * Connection details information.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"database", "version", "driver", "url", "user", "valid"})
    public static class ConnectionInfo {

        @JsonProperty("database")
        private String database;

        @JsonProperty("version")
        private String version;

        @JsonProperty("driver")
        private String driver;

        @JsonProperty("url")
        private String url;

        @JsonProperty("user")
        private String user;

        @JsonProperty("valid")
        private boolean valid;

        public ConnectionInfo() {}

        public String getDatabase() {
            return database;
        }

        public ConnectionInfo setDatabase(String database) {
            this.database = database;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public ConnectionInfo setVersion(String version) {
            this.version = version;
            return this;
        }

        public String getDriver() {
            return driver;
        }

        public ConnectionInfo setDriver(String driver) {
            this.driver = driver;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public ConnectionInfo setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getUser() {
            return user;
        }

        public ConnectionInfo setUser(String user) {
            this.user = user;
            return this;
        }

        public boolean isValid() {
            return valid;
        }

        public ConnectionInfo setValid(boolean valid) {
            this.valid = valid;
            return this;
        }
    }

    /**
     * Validation query information.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"query", "result"})
    public static class ValidationInfo {

        @JsonProperty("query")
        private String query;

        @JsonProperty("result")
        private String result;

        public ValidationInfo() {}

        public String getQuery() {
            return query;
        }

        public ValidationInfo setQuery(String query) {
            this.query = query;
            return this;
        }

        public String getResult() {
            return result;
        }

        public ValidationInfo setResult(String result) {
            this.result = result;
            return this;
        }
    }
}
