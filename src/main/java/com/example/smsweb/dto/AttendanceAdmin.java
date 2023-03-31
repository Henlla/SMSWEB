package com.example.smsweb.dto;

import com.example.smsweb.models.Student;
import com.example.smsweb.models.StudentClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceAdmin {
    private Integer isAttendance;
    private List<AttendanceEdit> listAttendanceEdit;
    private List<StudentClass> listStudentClass;
}
