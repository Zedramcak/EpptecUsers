package cz.zedramcak.epptecusers.exceptions;

public class UserDoesNotExistsException extends RuntimeException {
    public UserDoesNotExistsException(String message) {
        super(message);
    }
}
