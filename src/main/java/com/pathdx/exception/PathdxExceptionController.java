package com.pathdx.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PathdxExceptionController {

    @ExceptionHandler(value = LabNotFoundException.class)

    public ResponseEntity<Object> exception(LabNotFoundException exception) {
        return new ResponseEntity<>("Lab not found or given user does not belong to respective lab",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = OrderMessageNotFoundException.class)
    public ResponseEntity<Object> exception(OrderMessageNotFoundException exception) {
        return new ResponseEntity<>("Ordermessage not found for given Id",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<String> handleConflict(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
