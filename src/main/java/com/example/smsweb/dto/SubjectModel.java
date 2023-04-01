package com.example.smsweb.dto;

import com.example.smsweb.models.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectModel {
    private Integer semester;
    private List<Subject> listSubject;
}
