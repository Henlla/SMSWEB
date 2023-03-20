package com.example.smsweb.dto;

import com.example.smsweb.models.Subject;
import com.example.smsweb.models.Teacher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayInWeek {
    private Integer id;
    private LocalDate date;
    private Subject subject;
    private Integer subjectId;
    private String dayOfWeek;
    private Teacher teacher;
    private Integer month;
    private Integer slot;
    private Integer week;
    private Integer weekOfYear;
}
