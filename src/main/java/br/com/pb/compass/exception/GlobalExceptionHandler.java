package br.com.pb.compass.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalExceptions(Exception ex, WebRequest web){
        ErrorDetails details = new ErrorDetails();

        details.setTimeStamp(new Date());
        details.setMessage(ex.getMessage());
        details.setDetails(web.getDescription(false));

        return new ResponseEntity<>(details, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
