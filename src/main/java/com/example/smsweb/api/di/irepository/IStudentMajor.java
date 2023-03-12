package com.example.smsweb.api.di.irepository;

import com.example.smsweb.models.MajorStudent;

public interface IStudentMajor {
    MajorStudent findMajorStudentByStudentId(Integer studentId);
}
