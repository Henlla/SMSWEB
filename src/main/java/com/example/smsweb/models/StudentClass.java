package com.example.smsweb.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_class", schema = "smdb", catalog = "")
@Getter
@Setter
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
}
