package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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

    @Basic
    @Column(name = "start_date")
    private String startDate;
    @Basic
    @Column(name = "end_date")
    private String endDate;
    @Basic
    @Column(name = "on_deleted")
    private boolean onDeleted;
    @Basic
    @Column(name = "class_status")
    private String classStatus;

    @OneToMany(mappedBy = "classsesByClassId")
    @JsonManagedReference("class_schedule")
    private List<Schedule> schedulesById;

    @OneToMany(mappedBy = "classStudentByClass")
//    @JsonManagedReference("student_student_subject")
    private List<StudentClass> studentClassById;

    @ManyToOne
    @JoinColumn(name = "major_id" , insertable = false, updatable = false)
    private Major major;

    @ManyToOne
    @JoinColumn(name = "teacher_id" , insertable = false, updatable = false)
    //@JsonManagedReference("teacherClass")
    private Teacher teacher;


    public void setDefaultValues() {
        this.startDate = LocalDate.now().toString();
        this.onDeleted = false;
        this.classStatus = "active";
    }

}
