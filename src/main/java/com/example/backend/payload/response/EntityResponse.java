package com.example.backend.payload.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityResponse {

    private String title;
    private String message;
    private String error;
    private Object data;


    public EntityResponse(String title, String message, String error, Object data) {
        this.title = title;
        this.message = message;
        this.error = error;
        this.data = data;
    }
}
