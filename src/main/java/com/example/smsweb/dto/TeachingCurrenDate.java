package com.example.smsweb.dto;

import java.time.LocalDate;

import com.example.smsweb.models.Subject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeachingCurrenDate {
    private String date;
    private String dayOfWeek;
    private String classCode;
    private Subject subject;
    private String roomCode;
    private String shift;
    private Integer slot;
    private String time;
    private String startTime;
}
