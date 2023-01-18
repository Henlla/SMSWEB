package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

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
    @JsonBackReference(value = "applicationsById")
    private List<Application> applicationsById;
    @OneToMany(mappedBy = "studentByStudentId")
    @JsonBackReference(value = "majorStudentsById")
    private List<MajorStudent> majorStudentsById;
    @OneToMany(mappedBy = "studentByStudentId")
    @JsonBackReference(value = "studentSubjectsById")
    private List<StudentSubject> studentSubjectsById;
}
