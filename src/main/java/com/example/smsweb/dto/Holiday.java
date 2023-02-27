package com.example.smsweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Holiday {
    private String name;
    private LocalDate date;
    private LocalDate observed;
    private boolean publics;
    private String country;
    private String uuid;
    private Weekday weekday;
}
