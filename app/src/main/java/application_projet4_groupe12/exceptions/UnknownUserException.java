package application_projet4_groupe12.exceptions;

public class UnknownUserException extends RuntimeException {

    public UnknownUserException(String message){
        super(message);
    }
}