package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsonwebtoken.lang.Classes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_class", schema = "smdb", catalog = "")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentClass {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "student_id")
    private Integer studentId;
    @Basic
    @Column(name = "class_id")
    private Integer classId;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id",insertable = false,updatable = false)
    @JsonIgnore
    private Student classStudentByStudent;

    @ManyToOne
    @JoinColumn(name = "class_id", referencedColumnName = "id",insertable = false,updatable = false)
    @JsonIgnore
    private Classses classStudentByClass;
}
