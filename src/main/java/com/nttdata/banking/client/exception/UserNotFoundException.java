package com.nttdata.banking.client.exception;


/**
 * Exception named {@link UserNotFoundException} thrown when a requested user cannot be found.
 */
public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -3952215105519401565L;

    private static final String DEFAULT_MESSAGE = "User not found";

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public UserNotFoundException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
