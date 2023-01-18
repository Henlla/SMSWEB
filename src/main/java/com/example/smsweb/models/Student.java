package com.example.smsweb.models;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class Student {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "student_card")
    private String studentCard;
    @Basic
    @Column(name = "profile_id")
    private Integer profileId;
    @OneToMany(mappedBy = "studentByStudentId")
    private Collection<Application> applicationsById;
    @OneToMany(mappedBy = "studentByStudentId")
    private Collection<MajorStudent> majorStudentsById;
    @OneToMany(mappedBy = "studentByStudentId")
    private Collection<StudentSubject> studentSubjectsById;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentCard() {
        return studentCard;
    }

    public void setStudentCard(String studentCard) {
        this.studentCard = studentCard;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (id != student.id) return false;
        if (studentCard != null ? !studentCard.equals(student.studentCard) : student.studentCard != null) return false;
        if (profileId != null ? !profileId.equals(student.profileId) : student.profileId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (studentCard != null ? studentCard.hashCode() : 0);
        result = 31 * result + (profileId != null ? profileId.hashCode() : 0);
        return result;
    }

    public Collection<Application> getApplicationsById() {
        return applicationsById;
    }

    public void setApplicationsById(Collection<Application> applicationsById) {
        this.applicationsById = applicationsById;
    }

    public Collection<MajorStudent> getMajorStudentsById() {
        return majorStudentsById;
    }

    public void setMajorStudentsById(Collection<MajorStudent> majorStudentsById) {
        this.majorStudentsById = majorStudentsById;
    }

    public Collection<StudentSubject> getStudentSubjectsById() {
        return studentSubjectsById;
    }

    public void setStudentSubjectsById(Collection<StudentSubject> studentSubjectsById) {
        this.studentSubjectsById = studentSubjectsById;
    }
}
