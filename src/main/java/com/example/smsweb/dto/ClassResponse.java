package com.example.smsweb.dto;

public class ClassResponse {
    String status;
    String message;

    public ClassResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{\"status\":\"" + status + "\"," +
                "\"message\":\"" + message + "\"" +
                "}";
    }
}
