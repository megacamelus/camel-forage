package io.kaoto.forage.core.exceptions;

/**
 * Base runtime exception for the Forage framework.
 *
 * <p>This exception serves as the parent class for all unchecked exceptions thrown by
 * Forage components. It extends {@link RuntimeException} to allow exceptions to
 * propagate without requiring explicit handling in method signatures.
 *
 * <p>Subclasses include:
 * <ul>
 *   <li>{@link InvalidGuardrailException} - for guardrail configuration errors</li>
 * </ul>
 */
public class RuntimeForageException extends RuntimeException {
    public RuntimeForageException(String message) {
        super(message);
    }

    public RuntimeForageException() {
        super();
    }

    public RuntimeForageException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeForageException(Throwable cause) {
        super(cause);
    }

    protected RuntimeForageException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
