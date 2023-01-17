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
public class Attendance {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "status")
    private Integer status;
    @Basic
    @Column(name = "date")
    private String date;
    @Basic
    @Column(name = "student_subject_id")
    private Integer studentSubjectId;
    @Basic
    @Column(name = "note")
    private String note;
    @ManyToOne
    @JoinColumn(name = "student_subject_id", referencedColumnName = "id",insertable = false,updatable = false)
    @JsonManagedReference
    private StudentSubject studentSubjectByStudentSubjectId;

}
