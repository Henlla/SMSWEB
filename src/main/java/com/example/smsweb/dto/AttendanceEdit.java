package com.example.smsweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceEdit {
    private Integer attendance_id;
    private Integer student_id;
    private String note;
    private Integer isPresent;
    private String avatar;
    private String student_name;
}
