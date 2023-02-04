package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Classses {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "class_code")
    private String classCode;
    @Basic
    @Column(name = "limit_student")
    private Integer limitStudent;
    @Basic
    @Column(name = "teacher_id")
    private Integer teacherId;
    @Basic
    @Column(name = "major_id")
    private Integer majorId;
    @OneToMany(mappedBy = "classsesByClassId")
    @JsonManagedReference("class_schedule")
    private List<Schedule> schedulesById;

    @OneToMany(mappedBy = "classStudentByClass")
//    @JsonManagedReference("student_student_subject")
    private List<StudentClass> studentClassById;


}
