package io.kaoto.forage.core.exceptions;

/**
 * Exception thrown when a guardrail configuration is invalid or cannot be loaded.
 *
 * <p>This exception is thrown when:
 * <ul>
 *   <li>A guardrail class specified in configuration cannot be found or loaded</li>
 *   <li>A guardrail class does not implement the required interface</li>
 *   <li>A guardrail fails to initialize properly</li>
 * </ul>
 *
 * @see RuntimeForageException
 */
public class InvalidGuardrailException extends RuntimeForageException {
    public InvalidGuardrailException(String message) {
        super(message);
    }

    public InvalidGuardrailException(String message, Throwable cause) {
        super(message, cause);
    }
}
