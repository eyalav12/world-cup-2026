package com.world_cup.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException userNotFoundException){
        ErrorResponse errorResponse = new ErrorResponse(userNotFoundException.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BetTimeExpiredException.class)
    public ResponseEntity<ErrorResponse> handleBetTimeExpiredException(BetTimeExpiredException betTimeExpiredException){
        ErrorResponse errorResponse = new ErrorResponse(betTimeExpiredException.getMessage(),
                HttpStatus.BAD_REQUEST.value(),LocalDateTime.now());
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
}
