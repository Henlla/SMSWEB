package com.example.smsweb.dto;

import com.example.smsweb.models.Subject;
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
    private LocalDate date;
    private Subject subject;
    private Integer subjectId;
    private String dayOfWeek;
    private Integer month;
    private Integer week;
    private Integer weekOfYear;
}
