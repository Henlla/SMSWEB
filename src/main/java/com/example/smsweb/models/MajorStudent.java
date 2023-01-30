package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "major_student", schema = "smdb", catalog = "")
public class MajorStudent {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "major_id")
    private Integer majorId;
    @Basic
    @Column(name = "student_id")
    private Integer studentId;
    @ManyToOne
    @JoinColumn(name = "major_id", referencedColumnName = "id",insertable = false,updatable = false)
    @JsonBackReference("major_major_student")
    private Major majorByMajorId;
    @ManyToOne
    @JsonBackReference("student_major")
    @JoinColumn(name = "student_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Student studentByStudentId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getMajorId() {
        return majorId;
    }

    public void setMajorId(Integer majorId) {
        this.majorId = majorId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MajorStudent that = (MajorStudent) o;

        if (id != that.id) return false;
        if (majorId != null ? !majorId.equals(that.majorId) : that.majorId != null) return false;
        if (studentId != null ? !studentId.equals(that.studentId) : that.studentId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (majorId != null ? majorId.hashCode() : 0);
        result = 31 * result + (studentId != null ? studentId.hashCode() : 0);
        return result;
    }

    public Major getMajorByMajorId() {
        return majorByMajorId;
    }

    public void setMajorByMajorId(Major majorByMajorId) {
        this.majorByMajorId = majorByMajorId;
    }

    public Student getStudentByStudentId() {
        return studentByStudentId;
    }

    public void setStudentByStudentId(Student studentByStudentId) {
        this.studentByStudentId = studentByStudentId;
    }
}
