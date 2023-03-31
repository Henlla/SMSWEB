package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
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
    @Column(name = "update_times")
    private int updateTimes = 0;
    @Basic
    @Column(name = "student_subject_id")
    private Integer studentSubjectId;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "student_subject_id", referencedColumnName = "id",insertable = false,updatable = false)
    private StudentSubject studentSubjectByStudentSubjectId;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getAsm() {
        return asm;
    }

    public void setAsm(Double asm) {
        this.asm = asm;
    }

    public Double getObj() {
        return obj;
    }

    public void setObj(Double obj) {
        this.obj = obj;
    }

    public Integer getStudentSubjectId() {
        return studentSubjectId;
    }

    public void setStudentSubjectId(Integer studentSubjectId) {
        this.studentSubjectId = studentSubjectId;
    }

    public StudentSubject getStudentSubjectByStudentSubjectId() {
        return studentSubjectByStudentSubjectId;
    }

    public void setStudentSubjectByStudentSubjectId(StudentSubject studentSubjectByStudentSubjectId) {
        this.studentSubjectByStudentSubjectId = studentSubjectByStudentSubjectId;
    }
}
