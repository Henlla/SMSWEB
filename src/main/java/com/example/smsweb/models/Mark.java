package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mark mark = (Mark) o;

        if (id != mark.id) return false;
        if (asm != null ? !asm.equals(mark.asm) : mark.asm != null) return false;
        if (obj != null ? !obj.equals(mark.obj) : mark.obj != null) return false;
        if (studentSubjectId != null ? !studentSubjectId.equals(mark.studentSubjectId) : mark.studentSubjectId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (asm != null ? asm.hashCode() : 0);
        result = 31 * result + (obj != null ? obj.hashCode() : 0);
        result = 31 * result + (studentSubjectId != null ? studentSubjectId.hashCode() : 0);
        return result;
    }

    public StudentSubject getStudentSubjectByStudentSubjectId() {
        return studentSubjectByStudentSubjectId;
    }

    public void setStudentSubjectByStudentSubjectId(StudentSubject studentSubjectByStudentSubjectId) {
        this.studentSubjectByStudentSubjectId = studentSubjectByStudentSubjectId;
    }
}
