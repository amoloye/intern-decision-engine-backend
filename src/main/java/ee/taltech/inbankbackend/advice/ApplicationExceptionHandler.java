package ee.taltech.inbankbackend.advice;

import ee.taltech.inbankbackend.exceptions.InvalidPersonalCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

public class ApplicationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleInvalidArgument(MethodArgumentNotValidException ex){
        Map<String,String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error-> errorMap.put(error.getField(),error.getDefaultMessage()));
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPersonalCodeException.class)
    public Map<String,String> handleBusinessException(InvalidPersonalCodeException exception){
        Map<String,String> errorMap= new HashMap<>();
        errorMap.put("errorMessage", exception.getMessage());
        return  errorMap;
    }
}
