package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
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
    @JsonManagedReference(value = "studentSubjectByStudentSubjectId")
    private StudentSubject studentSubjectByStudentSubjectId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getStudentSubjectId() {
        return studentSubjectId;
    }

    public void setStudentSubjectId(Integer studentSubjectId) {
        this.studentSubjectId = studentSubjectId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attendance that = (Attendance) o;

        if (id != that.id) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (studentSubjectId != null ? !studentSubjectId.equals(that.studentSubjectId) : that.studentSubjectId != null)
            return false;
        if (note != null ? !note.equals(that.note) : that.note != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (studentSubjectId != null ? studentSubjectId.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        return result;
    }

    public StudentSubject getStudentSubjectByStudentSubjectId() {
        return studentSubjectByStudentSubjectId;
    }

    public void setStudentSubjectByStudentSubjectId(StudentSubject studentSubjectByStudentSubjectId) {
        this.studentSubjectByStudentSubjectId = studentSubjectByStudentSubjectId;
    }
}
