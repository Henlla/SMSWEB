package com.example.smsweb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Classses {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "class_code", nullable = true, length = 45)
    private String classCode;
    @Basic
    @Column(name = "limit_student", nullable = true)
    private Integer limitStudent;
    @Basic
    @Column(name = "teacher_id", nullable = true)
    private Integer teacherId;
    @Basic
    @Column(name = "major_id", nullable = true)
    private Integer majorId;
    @Basic
    @Column(name = "start_date", nullable = true, length = 45)
    private String startDate;
    @Basic
    @Column(name = "end_date", nullable = true, length = 45)
    private String endDate;
    @Basic
    @Column(name = "on_deleted", nullable = true)
    private boolean onDeleted;
    @Basic
    @Column(name = "class_status", nullable = true, length = 45)
    private String classStatus;
    @Basic
    @Column(name = "shift", nullable = true, length = 45)
    private String shift;
    @Basic
    @Column(name = "room_id", nullable = true)
    private Integer roomId;
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
    //@JsonManagedReference("teacherClass")
    private Room classRoom;
    @Basic
    @Column(name = "department_id", nullable = true)
    private Integer departmentId;
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id",insertable = false, updatable = false)
    private Department departmentByDepartmentId;


}
