package com.world_cup.demo.exception;

public class BetTimeExpiredException extends RuntimeException{
    public BetTimeExpiredException(String message){
        super(message);
    }
}
