package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Major {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "major_code")
    private String majorCode;
    @Basic
    @Column(name = "major_name")
    private String majorName;
    @OneToMany(mappedBy = "majorByMajorId")
    @JsonManagedReference("majorMajorStudentBackReference")
    private Collection<MajorStudent> majorStudentsById;
    @OneToMany(mappedBy = "majorByMajorId")
    @JsonManagedReference("subjectMajorBackReference")
    private Collection<Subject> subjectsById;
}
