package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username")
        })
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(name = "username")
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "email")
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password")
    private String password;

    @NotBlank
    @Size(max = 15)
    @Column(name = "first_name")
    private String firstName;

    @NotBlank
    @Size(max = 15)
    @Column(name = "last_name")
    private String lastName;

    @NotBlank
    @Column(name = "phone_number")
    private String phoneNumber;


    @Column(name = "government")
    private String government;


    @Column(name = "city")
    private String city;

    @OneToOne
    @JoinColumn(name = "Category_id")
    private Category category;


    @Column(name = "image")
    private String imageUrl;


    @Column(name = "otp_code")
    private String otpCode;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "prestataire", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Offer> offers;

    @OneToMany(mappedBy = "client", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<TimeSlot> timeSlots;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.REMOVE)
    private List<PhoneToken> phoneToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Notification> notifications;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String email, String password, String firstName, String lastName, String phoneNumber, String government, String city, Category category, String imageUrl) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.government = government;
        this.city = city;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public User(String firstName, String lastName, String phoneNumber, String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;

    }
}
