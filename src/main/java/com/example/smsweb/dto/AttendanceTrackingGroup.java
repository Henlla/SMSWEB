package com.example.smsweb.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceTrackingGroup {
    private String date;
    private List<AttendanceTrackingModel>list;
}
