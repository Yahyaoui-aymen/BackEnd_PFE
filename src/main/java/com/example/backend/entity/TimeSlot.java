package com.example.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "time_slot")
@Getter
@Setter
@NoArgsConstructor
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "time")
    private ETimeSlot time;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private User provider;

    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Appointment> appointments;

    public TimeSlot(ETimeSlot time) {
        this.time = time;
    }

}
