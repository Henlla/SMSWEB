package com.example.smsweb.api.di.irepository;

import com.example.smsweb.api.generic.IGenericRepository;
import com.example.smsweb.models.Classses;

import java.util.List;

public interface IClass extends IGenericRepository<Classses> {
    Classses findByClassCode(String classCode);
    List<String> searchClasssesByClassCode(String listStudentCard);
    List<Classses> findClassByTeacherId(Integer id);
    Classses findClassByTeacherIdAndScheduleId(Integer teacherId, Integer scheduleId);
    List<Classses> findClassByMajorId(Integer majorId);
    List<Classses> findClassByRoom(Integer roomId);
    List<Classses> findClassByRoomAndDepartmentId(Integer roomId,Integer departmentId);
    List<Classses> findClassesByShift(String shift);
}
