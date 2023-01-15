package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "student_card")
    private String studentCard;
    @Basic
    @Column(name = "profile_id")
    private Integer profileId;
    @OneToMany(mappedBy = "studentByStudentId")
    @JsonBackReference
    private Collection<Application> applicationsById;
    @OneToMany(mappedBy = "studentByStudentId")
    @JsonBackReference
    private Collection<MajorStudent> majorStudentsById;
    @OneToMany(mappedBy = "studentByStudentId")
    @JsonBackReference
    private Collection<StudentSubject> studentSubjectsById;
}
