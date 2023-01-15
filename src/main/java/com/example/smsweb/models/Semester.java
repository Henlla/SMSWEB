package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Semester {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "semester_code")
    private String semesterCode;
    @Basic
    @Column(name = "major_id")
    private Integer majorId;
    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "major_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Major majorByMajorId;
}
