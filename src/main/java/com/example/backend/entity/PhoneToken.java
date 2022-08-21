package com.example.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "phoneToken")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhoneToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;


    @NotBlank
    @Column(name = "token")
    private String token ;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user ;

}
