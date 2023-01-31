package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "profile_id")
    private Integer profileId;
    @OneToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Profile profileByProfileId;

}
