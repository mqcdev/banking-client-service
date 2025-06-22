package com.nttdata.banking.client.exception;


/**
 * Exception named {@link UserStatusNotValidException} thrown when a user's status is not valid for the current operation.
 */
public class UserStatusNotValidException extends RuntimeException {

    private static final long serialVersionUID = 3440177088502218750L;

    private static final String DEFAULT_MESSAGE = "User status is not valid for the current operation.";

    /**
     * Constructs a {@code UserStatusNotValidException} with the default message.
     */
    public UserStatusNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a {@code UserStatusNotValidException} with a custom message.
     *
     * @param message the detail message
     */
    public UserStatusNotValidException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
