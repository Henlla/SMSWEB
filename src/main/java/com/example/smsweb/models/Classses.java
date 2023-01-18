package com.example.smsweb.models;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;

@Entity
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
    @JsonBackReference(value = "schedulesById")
    private List<Schedule> schedulesById;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public Integer getLimitStudent() {
        return limitStudent;
    }

    public void setLimitStudent(Integer limitStudent) {
        this.limitStudent = limitStudent;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
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

        Classses classses = (Classses) o;

        if (id != classses.id) return false;
        if (classCode != null ? !classCode.equals(classses.classCode) : classses.classCode != null) return false;
        if (limitStudent != null ? !limitStudent.equals(classses.limitStudent) : classses.limitStudent != null)
            return false;
        if (teacherId != null ? !teacherId.equals(classses.teacherId) : classses.teacherId != null) return false;
        if (majorId != null ? !majorId.equals(classses.majorId) : classses.majorId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (classCode != null ? classCode.hashCode() : 0);
        result = 31 * result + (limitStudent != null ? limitStudent.hashCode() : 0);
        result = 31 * result + (teacherId != null ? teacherId.hashCode() : 0);
        result = 31 * result + (majorId != null ? majorId.hashCode() : 0);
        return result;
    }

    public Collection<Schedule> getSchedulesById() {
        return schedulesById;
    }

    public void setSchedulesById(Collection<Schedule> schedulesById) {
        this.schedulesById = schedulesById;
    }
}
