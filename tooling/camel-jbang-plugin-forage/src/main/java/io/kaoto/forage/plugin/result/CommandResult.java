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
package io.kaoto.forage.plugin.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Base class for all command results. Provides a common JSON structure
 * for success and failure responses across all Forage CLI commands.
 *
 * <p>Success response example:
 * <pre>{@code
 * {
 *   "success": true,
 *   "message": "Operation completed successfully"
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
@JsonPropertyOrder({"success", "message", "error"})
public class CommandResult {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @JsonProperty("success")
    private final boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("error")
    private String error;

    protected CommandResult(boolean success) {
        this.success = success;
    }

    /**
     * Creates a successful result with a message.
     */
    public static CommandResult success(String message) {
        CommandResult result = new CommandResult(true);
        result.message = message;
        return result;
    }

    /**
     * Creates a failed result with an error message.
     */
    public static CommandResult failure(String errorMessage) {
        CommandResult result = new CommandResult(false);
        result.error = errorMessage;
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    protected void setErrorMessage(String error) {
        this.error = error;
    }

    /**
     * Converts the result to a JSON string.
     */
    public String toJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // Fallback to manual JSON if serialization fails
            if (success) {
                return "{\"success\": true, \"message\": \"" + escapeJson(message) + "\"}";
            } else {
                return "{\"success\": false, \"error\": \"" + escapeJson(error != null ? error : "Unknown error")
                        + "\"}";
            }
        }
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
