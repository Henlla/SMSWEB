package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Classses {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
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
    @Basic
    @Column(name = "shift")
    private String shift;

    @Basic
    @Column(name = "room_id")
    private Integer roomId;

    public Classses() {
        setDefaultValues();
    }

    @OneToMany(mappedBy = "classsesByClassId")
    @JsonManagedReference("class_schedule")
    private List<Schedule> schedulesById;

    @OneToMany(mappedBy = "classStudentByClass")
//    @JsonManagedReference("student_student_subject")
    private List<StudentClass> studentClassById;

    @ManyToOne
    @JoinColumn(name = "major_id", insertable = false, updatable = false)
    private Major major;

    @ManyToOne
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    //@JsonManagedReference("teacherClass")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Room classRoom;


    public void setDefaultValues() {
        this.onDeleted = false;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
