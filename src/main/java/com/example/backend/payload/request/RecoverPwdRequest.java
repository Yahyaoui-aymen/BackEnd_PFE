package com.example.backend.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoverPwdRequest {

    private String username;

    private String phoneNumber;
}
