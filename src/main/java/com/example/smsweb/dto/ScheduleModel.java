package com.example.smsweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleModel {
    private Integer id;
    private LocalDate endDate;
    private LocalDate startDate;
    private Integer semester;
    private List<DayInWeek> dayInWeeks;
}

