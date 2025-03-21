package ee.taltech.inbankbackend.exceptions;

public class InvalidPersonalCodeException extends Exception{
    public InvalidPersonalCodeException (String message) {
        super(message);
    }
}
