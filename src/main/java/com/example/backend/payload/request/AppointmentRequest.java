package com.example.backend.payload.request;

import com.example.backend.entity.Appointment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
public class AppointmentRequest {


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date date;


    private Integer timeSlotId;

    private Boolean isOffer ;
}
