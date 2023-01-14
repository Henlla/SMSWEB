package com.example.smsweb.models;

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
public class Subject {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "subject_code")
    private String subjectCode;
    @Basic
    @Column(name = "subject_name")
    private String subjectName;
    @Basic
    @Column(name = "fee")
    private Double fee;
    @Basic
    @Column(name = "semester")
    private Integer semester;
    @OneToMany(mappedBy = "subjectBySubjectId")
    private Collection<Major> majorsById;
    @OneToMany(mappedBy = "subjectBySubjectId")
    private Collection<ScheduleDetail> scheduleDetailsById;
    @OneToMany(mappedBy = "subjectBySubjectId")
    private Collection<StudentSubject> studentSubjectsById;
}
