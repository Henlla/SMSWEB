package com.example.smsweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectView {
    private Integer id;
    private String subject_code;
    private String subject_name;
    private String status;
    private Integer semester;
}
