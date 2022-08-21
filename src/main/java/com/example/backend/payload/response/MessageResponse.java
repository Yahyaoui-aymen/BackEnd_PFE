package com.example.backend.payload.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageResponse {
    private String title;
    private String message;
    private String error;

    public MessageResponse(String title, String message, String error) {
        this.title = title;
        this.message = message;
        this.error = error;
    }
}
