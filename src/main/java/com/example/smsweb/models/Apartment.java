package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Apartment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "apartment_code",nullable = true,length = 45)
    private String apartmentCode;
    @Basic
    @Column(name = "apartment_name", nullable = true, length = 45)
    private String apartmentName;
    @OneToMany(mappedBy = "apartmentByApartmentId")
    @JsonIgnore
    private Collection<Major> majorsById;

}
