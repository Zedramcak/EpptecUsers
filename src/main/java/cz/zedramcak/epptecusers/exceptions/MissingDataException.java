package cz.zedramcak.epptecusers.exceptions;

public class MissingDataException extends RuntimeException {
    public MissingDataException(String message) {
        super(message);
    }
}
