package org.apache.camel.forage.core.exceptions;

/**
 * Exception thrown when no suitable agent can be found for handling a request.
 *
 * <p>This exception is raised when the agent discovery mechanism cannot locate
 * an appropriate agent implementation based on the provided criteria (route ID,
 * header value, property value, or variable value). It includes factory methods
 * for creating contextual error messages based on different agent ID sources.
 */
public class UndefinedAgentException extends ForageException {
    public UndefinedAgentException() {}

    public UndefinedAgentException(String message) {
        super(message);
    }

    public UndefinedAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndefinedAgentException(Throwable cause) {
        super(cause);
    }

    public UndefinedAgentException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static UndefinedAgentException fromRouteId(String routeId) {
        return new UndefinedAgentException(
                String.format("Route id '%s' has no defined agent capable of handling this request", routeId));
    }

    public static UndefinedAgentException fromHeader(String headerName, String headerValue) {
        return new UndefinedAgentException(String.format(
                "Header '%s' with value '%s' has no defined agent capable of handling this request",
                headerName, headerValue));
    }

    public static UndefinedAgentException fromProperty(String propertyName, String propertyValue) {
        return new UndefinedAgentException(String.format(
                "Property '%s' with value '%s' has no defined agent capable of handling this request",
                propertyName, propertyValue));
    }

    public static UndefinedAgentException fromVariable(String variableName, String variableValue) {
        return new UndefinedAgentException(String.format(
                "Variable '%s' with value '%s' has no defined agent capable of handling this request",
                variableName, variableValue));
    }
}
