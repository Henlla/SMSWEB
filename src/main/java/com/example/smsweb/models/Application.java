package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
public class Application {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "send_date")
    private String sendDate;
    @Basic
    @Column(name = "status")
    private String status;
    @Basic
    @Column(name = "file")
    private String file;
    @Basic
    @Column(name = "student_id")
    private Integer studentId;
    @Basic
    @Column(name = "application_type_id")
    private Integer applicationTypeId;
    @ManyToOne
    @JsonBackReference("application_student")
    @JoinColumn(name = "student_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Student studentByStudentId;
    @ManyToOne
    @JsonBackReference("application_application_type")
    @JoinColumn(name = "application_type_id", referencedColumnName = "id",insertable = false,updatable = false)
    private ApplicationType applicationTypeByApplicationTypeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getApplicationTypeId() {
        return applicationTypeId;
    }

    public void setApplicationTypeId(Integer applicationTypeId) {
        this.applicationTypeId = applicationTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Application that = (Application) o;

        if (id != that.id) return false;
        if (sendDate != null ? !sendDate.equals(that.sendDate) : that.sendDate != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (file != null ? !file.equals(that.file) : that.file != null) return false;
        if (studentId != null ? !studentId.equals(that.studentId) : that.studentId != null) return false;
        if (applicationTypeId != null ? !applicationTypeId.equals(that.applicationTypeId) : that.applicationTypeId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (sendDate != null ? sendDate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + (studentId != null ? studentId.hashCode() : 0);
        result = 31 * result + (applicationTypeId != null ? applicationTypeId.hashCode() : 0);
        return result;
    }

    public Student getStudentByStudentId() {
        return studentByStudentId;
    }

    public void setStudentByStudentId(Student studentByStudentId) {
        this.studentByStudentId = studentByStudentId;
    }

    public ApplicationType getApplicationTypeByApplicationTypeId() {
        return applicationTypeByApplicationTypeId;
    }

    public void setApplicationTypeByApplicationTypeId(ApplicationType applicationTypeByApplicationTypeId) {
        this.applicationTypeByApplicationTypeId = applicationTypeByApplicationTypeId;
    }
}
