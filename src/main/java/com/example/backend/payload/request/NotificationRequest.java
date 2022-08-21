package com.example.backend.payload.request;

import com.example.backend.entity.PhoneToken;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Getter
@Setter
public class NotificationRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    private Long userId;
}
