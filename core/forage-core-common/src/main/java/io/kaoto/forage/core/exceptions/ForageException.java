package io.kaoto.forage.core.exceptions;

/**
 * Base exception class for all Forage framework-specific errors.
 *
 * <p>This exception serves as the root of the Forage exception hierarchy,
 * providing a common base for all framework-related exceptions. It extends
 * {@code Exception} to represent checked exceptions that must be handled
 * or declared by calling code.
 */
public class ForageException extends Exception {

    public ForageException() {}

    public ForageException(String message) {
        super(message);
    }

    public ForageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForageException(Throwable cause) {
        super(cause);
    }

    public ForageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
