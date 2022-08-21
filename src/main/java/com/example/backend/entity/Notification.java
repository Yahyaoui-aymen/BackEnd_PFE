package com.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id ;

    @NotBlank
    @Column(name = "title")
    private String title;

    @NotBlank
    @Column(name = "message")
    private String message;

    @Column(name = "date")
    private Date date = new Date() ;


    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;


    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
    }

}
