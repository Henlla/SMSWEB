package com.example.smsweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SumAttendance {
    private String date;
    private Integer slot;
    private String teacher_name;
    private String class_name;
    private String status;
}
