package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
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
    @Column(name = "semester_id")
    private Integer semesterId;
    @Basic
    @Column(name = "major_id")
    private Integer majorId;
    @OneToMany(mappedBy = "subjectBySubjectId")
    @JsonManagedReference("subject_schedule_detail")
    private Collection<ScheduleDetail> scheduleDetailsById;
    @OneToMany(mappedBy = "subjectBySubjectId")
    @JsonManagedReference("subject_student_subject")
    private Collection<StudentSubject> studentSubjectsById;
    @ManyToOne
    @JoinColumn(name = "semester_id", referencedColumnName = "id",insertable = false,updatable = false)
    @JsonBackReference("subject_semester")
    private Semester semesterBySemesterId;
    @ManyToOne
    @JoinColumn(name = "major_id", referencedColumnName = "id",insertable = false,updatable = false)
    @JsonBackReference("subject_major")
    private Major majorByMajorId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Integer getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Integer semesterId) {
        this.semesterId = semesterId;
    }

    public Integer getMajorId() {
        return majorId;
    }

    public void setMajorId(Integer majorId) {
        this.majorId = majorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subject subject = (Subject) o;

        if (id != subject.id) return false;
        if (subjectCode != null ? !subjectCode.equals(subject.subjectCode) : subject.subjectCode != null) return false;
        if (subjectName != null ? !subjectName.equals(subject.subjectName) : subject.subjectName != null) return false;
        if (fee != null ? !fee.equals(subject.fee) : subject.fee != null) return false;
        if (semesterId != null ? !semesterId.equals(subject.semesterId) : subject.semesterId != null) return false;
        if (majorId != null ? !majorId.equals(subject.majorId) : subject.majorId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (subjectCode != null ? subjectCode.hashCode() : 0);
        result = 31 * result + (subjectName != null ? subjectName.hashCode() : 0);
        result = 31 * result + (fee != null ? fee.hashCode() : 0);
        result = 31 * result + (semesterId != null ? semesterId.hashCode() : 0);
        result = 31 * result + (majorId != null ? majorId.hashCode() : 0);
        return result;
    }

    public Collection<ScheduleDetail> getScheduleDetailsById() {
        return scheduleDetailsById;
    }

    public void setScheduleDetailsById(Collection<ScheduleDetail> scheduleDetailsById) {
        this.scheduleDetailsById = scheduleDetailsById;
    }

    public Collection<StudentSubject> getStudentSubjectsById() {
        return studentSubjectsById;
    }

    public void setStudentSubjectsById(Collection<StudentSubject> studentSubjectsById) {
        this.studentSubjectsById = studentSubjectsById;
    }

    public Semester getSemesterBySemesterId() {
        return semesterBySemesterId;
    }

    public void setSemesterBySemesterId(Semester semesterBySemesterId) {
        this.semesterBySemesterId = semesterBySemesterId;
    }

    public Major getMajorByMajorId() {
        return majorByMajorId;
    }

    public void setMajorByMajorId(Major majorByMajorId) {
        this.majorByMajorId = majorByMajorId;
    }
}
