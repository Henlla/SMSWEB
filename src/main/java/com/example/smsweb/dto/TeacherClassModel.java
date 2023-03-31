package com.example.smsweb.dto;

import com.example.smsweb.models.Classses;
import com.example.smsweb.models.Teacher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherClassModel {
    private Teacher teacher;
    private List<Classses> classses;
}
