package application_projet4_groupe12.exceptions;

public class UnknownUserException extends Exception {

    public UnknownUserException(String message){
        super(message);
    }
}