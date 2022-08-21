package com.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "ads")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ads {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title")
    private String title;

    @NotBlank
    @Column(name = "description")
    private String description;

    @NotBlank
    @Column(name = "media")
    private String media;


    public Ads(String title, String description, String media) {
        this.title = title;
        this.description = description;
        this.media = media;
    }

    @Override
    public String toString() {
        return "Ads{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", media='" + media + '\'' +
                '}';
    }
}
