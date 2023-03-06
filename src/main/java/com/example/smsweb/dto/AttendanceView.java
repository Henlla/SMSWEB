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
    private Integer class_id;
    private String class_name;
    private String date;
    private String subject_name;
    private String subject_code;
}
