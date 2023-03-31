package com.example.smsweb.dto;

import com.example.smsweb.models.Classses;
import com.example.smsweb.models.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentClassModel {
    private Student student;
    private List<Classses> classes;
}
