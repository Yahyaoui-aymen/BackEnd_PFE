package com.example.backend.payload.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CountResponse {

    private Integer clientNumber;
    private Integer providerNumber;
    private Integer moderatorNumber;
}
