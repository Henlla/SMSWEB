package com.example.smsweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceView {
    private String class_id;
    private String class_name;
    private String date;
    private Integer isAttendance;
    private Integer slot;
    private String shift;
    private Integer onTime;
}
