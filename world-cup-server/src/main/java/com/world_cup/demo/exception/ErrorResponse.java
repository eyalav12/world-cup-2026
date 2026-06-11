package com.world_cup.demo.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ErrorResponse() {
    }

    private int status;

    public ErrorResponse(String message, int status, LocalDateTime timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    private LocalDateTime timestamp;
}
