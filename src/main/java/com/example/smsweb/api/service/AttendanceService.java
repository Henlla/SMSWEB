package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.IAttendance;
import com.example.smsweb.api.di.repository.AttendanceRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Attendance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AttendanceService implements IAttendance {
    @Autowired
    private AttendanceRepository attendanceDao;
    List<Attendance> listAttendance;
    Attendance attendance;

    @Override
    public void save(Attendance attendance) {
        try {
            attendanceDao.save(attendance);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorHandler("Save fail");
        }
    }

    @Override
    public void delete(int id) {
        try {
            attendanceDao.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorHandler("Delete fail");
        }
    }

    @Override
    public List<Attendance> findAll() {
        try {
            return attendanceDao.findAll();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public Attendance findOne(int id) {
        try {
            return attendanceDao.findById(id).get();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorHandler("Don't find attendance with id " + id);
        }
    }

    @Override
    public void saveAllAttendance(List<Attendance> attendances) {
        try {
            attendanceDao.saveAll(attendances);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorHandler("Save fail");
        }
    }

    @Override
    public List<Attendance> findAttendByDate(String date) {
        return attendanceDao.findAttendancesByDate(date).orElseThrow(() -> new ErrorHandler("Don't find any records"));
    }

    @Override
    public List<Attendance> findAttendanceByDateAndSlot(String date, Integer slot) {
        return attendanceDao.findAttendancesByDateAndSlot(date, slot).orElseThrow(() -> new ErrorHandler("Don't find any records"));
    }

    @Override
    public Attendance findAttendanceByDateSlotStudentSubject(String date, String slot, String student_subject) {
        try {
            attendance = new Attendance();
            attendance = attendanceDao.findAttendanceByDateAndSlotAndStudentSubjectId(date, Integer.valueOf(slot), Integer.valueOf(student_subject));
            if (attendance != null) {
                return attendance;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorHandler("Don't have data");
        }
    }

    @Override
    public List<Attendance> findAttendanceByStudentSubjectId(Integer studentSubjectId) {
        try {
            return attendanceDao.findAttendancesByStudentSubjectId(studentSubjectId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorHandler("Don't have data");
        }
    }

    @Override
    public List<Attendance> findAttendancesByDateAndSlotAndStudentSubjectId(String date, Integer slot, Integer studentSubjectId) {
        try {
            listAttendance = new ArrayList<>();
            listAttendance = attendanceDao.findAttendancesByDateAndSlotAndStudentSubjectId(date, slot, studentSubjectId);
            if (listAttendance.size() == 0) {
                return null;
            } else {
                return listAttendance;
            }
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public List<Attendance> findAttendancesByDateAndSlotAndStudentSubjectIdAndShift(String date, Integer slot, Integer studentSubjectId, String shift) {
        try {
            listAttendance = new ArrayList<>();
            listAttendance = attendanceDao.findAttendancesByDateAndSlotAndStudentSubjectIdAndShift(date, slot, studentSubjectId, shift);
            if (listAttendance.isEmpty()) {
                return null;
            } else {
                return listAttendance;
            }
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public Attendance findAttendanceByDateAndSlotAndStudentSubjectIdAndShift(String date, Integer slot, Integer studentSubjectId, String shift) {
        try {
            attendance = new Attendance();
            attendance = attendanceDao.findAttendanceByDateAndSlotAndStudentSubjectIdAndShift(date, slot, studentSubjectId, shift);
            if (attendance == null) {
                return null;
            } else {
                return attendance;
            }
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }

    @Override
    public List<Attendance> findAttendanceByDateAndSlotAndShift(String date, Integer slot, String shift) {
        try {
            listAttendance = new ArrayList<>();
            listAttendance = attendanceDao.findAttendancesByDateAndSlotAndShift(date, slot, shift);
            if (listAttendance.isEmpty()) {
                return null;
            } else {
                return listAttendance;
            }
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }
}
