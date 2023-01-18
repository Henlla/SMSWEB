package com.example.smsweb.api.exception;

import com.example.smsweb.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ErrorHandler.class)
    public ResponseEntity<?> handlerSaveData(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse("error ", LocalDateTime.now().toString()));
    }
}
