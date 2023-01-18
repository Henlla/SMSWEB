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
public class Mark {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "asm")
    private Double asm;
    @Basic
    @Column(name = "obj")
    private Double obj;
    @Basic
    @Column(name = "student_subject_id")
    private Integer studentSubjectId;
    @ManyToOne
    @JoinColumn(name = "student_subject_id", referencedColumnName = "id",insertable = false,updatable = false)
    @JsonManagedReference
    private StudentSubject studentSubjectByStudentSubjectId;
}
