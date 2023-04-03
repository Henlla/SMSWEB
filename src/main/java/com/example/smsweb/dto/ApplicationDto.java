package com.example.smsweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDto {
    private int id;
    private String sendDate;
    private String status;
    private String note;
    private String file;
    private String responseNote;
    private String responseDate;
    private String studentName;
    private String studentCard;
}
