package com.nttdata.banking.client.exception;


/**
 * Exception named {@link PasswordNotValidException} thrown when a password does not meet the required criteria.
 */
public class PasswordNotValidException extends RuntimeException {

    private static final long serialVersionUID = 7389659106153108528L;

    private static final String DEFAULT_MESSAGE = "The password does not meet the required criteria.";

    /**
     * Constructs a {@code PasswordNotValidException} with the default message.
     */
    public PasswordNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a {@code PasswordNotValidException} with a custom message.
     *
     * @param message the detail message
     */
    public PasswordNotValidException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
