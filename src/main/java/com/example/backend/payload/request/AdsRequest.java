package com.example.backend.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AdsRequest {

    private String title;
    private String description;
    private String media;
}
