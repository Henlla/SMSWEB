package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.ISubject;
import com.example.smsweb.api.di.repository.MajorRepository;
import com.example.smsweb.api.di.repository.SemesterRepository;
import com.example.smsweb.api.di.repository.SubjectResponsitory;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Major;
import com.example.smsweb.models.Semester;
import com.example.smsweb.models.Subject;
import com.example.smsweb.utils.ExcelHelper;
import com.example.smsweb.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SubjectService implements ISubject {
    @Autowired
    SubjectResponsitory subjectDao;
    @Autowired
    MajorRepository majorDao;
    @Autowired
    SemesterRepository semesterDao;
    List<Subject> listSubject;
    Major major;
    List<Subject> existsSubject;
    Optional<Subject> subject;
    Semester semester;
    XSSFWorkbook workbook;
    XSSFSheet sheet;

    @Override
    public void save(Subject subject) {
        try {
            subjectDao.save(subject);
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            subjectDao.deleteById(id);
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public List<Subject> findAll() {
        try {
            return subjectDao.findAll();
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public Subject findOne(int id) {
        return subjectDao.findById(id).orElseThrow(() -> new ErrorHandler("do not found any subject with id: " + id));
    }

    @Override
    public List<Subject> findSubjectByMajorId(int majorId) {
        try {
            return subjectDao.findAllByMajorId(majorId);
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @Override
    public String importExcelData(MultipartFile file) {
        String status = "";
        if (!file.isEmpty()) {
            if (FileUtils.getExtension(file.getOriginalFilename()).equals("xlsx")) {
                listSubject = new ArrayList<>();
                try {
                    workbook = new XSSFWorkbook(file.getInputStream());
                    sheet = workbook.getSheetAt(0);
                    for (int rowIndex = 2; rowIndex < ExcelHelper.getNumberOfNonEmptyCells(sheet, 0); rowIndex++) {
                        XSSFRow row = sheet.getRow(rowIndex);
                        String subject_code = ExcelHelper.getValue(row.getCell(0)).toString() == null ? "" : ExcelHelper.getValue(row.getCell(0)).toString();
                        String subject_name = ExcelHelper.getValue(row.getCell(1)).toString() == null ? "" : ExcelHelper.getValue(row.getCell(1)).toString();
                        String subject_fee = ExcelHelper.getValue(row.getCell(2)).toString() == null ? "" : ExcelHelper.getValue(row.getCell(2)).toString();
                        String subject_slot = ExcelHelper.getValue(row.getCell(3)).toString() == null ? "" : ExcelHelper.getValue(row.getCell(3)).toString();
                        String subject_semester = ExcelHelper.getValue(row.getCell(4)).toString() == null ? "" : ExcelHelper.getValue(row.getCell(4)).toString();
                        String subject_major = ExcelHelper.getValue(row.getCell(5)).toString() == null ? "" : ExcelHelper.getValue(row.getCell(5)).toString();
                        if (!subject_code.isEmpty() && !subject_name.isEmpty()
                                && !subject_fee.isEmpty() && !subject_slot.isEmpty()
                                && !subject_semester.isEmpty() && !subject_major.isEmpty()) {
                            major = new Major();
                            subject = Optional.of(new Subject());
                            semester = new Semester();
                            major = majorDao.findMajorByMajorCode(subject_major.trim());
                            semester = semesterDao.findBySemesterCode(subject_semester.trim());
                            if (major != null && semester != null) {
                                subject = subjectDao.findSubjectBySubjectCode(major.getMajorId() + "-" + subject_code);
                                if (subject.isEmpty()) {
                                    Subject subject = Subject.builder().subjectCode(major.getMajorId() + "-" + subject_code)
                                            .subjectName(subject_name).fee(Double.valueOf(subject_fee))
                                            .slot(Integer.valueOf(subject_slot)).semesterId(semester.getId()).majorId(major.getId()).build();
                                    listSubject.add(subject);
                                } else {
                                    status = "Subject in excel at row " + (rowIndex + 1) + " is existed";
                                    break;
                                }
                            } else {
                                status = "Don't find major or semester data at row " + (rowIndex + 1);
                                break;
                            }
                        } else {
                            status = "Row " + (rowIndex + 1) + " don't have data";
                            break;
                        }
                    }
                    if(!status.isEmpty()){
                        return status;
                    }else{
                        subjectDao.saveAll(listSubject);
                        return "";
                    }
                } catch (Exception e) {
                    log.error("Import Subject: " + e.getMessage());
                    return "Import data fail";
                }
            } else {
                return "Please choose excel file";
            }
        } else {
            return "Please choose file";
        }
    }

    @Override
    public Subject findSubjectBySubjectCode(String subjectCode) {
        return subjectDao.findSubjectBySubjectCode(subjectCode).orElseThrow(() -> new ErrorHandler("Subject " + subjectCode + " is not existed"));
    }

    public List<Subject> findSubjectByMajorIdSemester(Integer majorId, Integer semester) {
        return subjectDao.findAllByMajorIdAndSemesterId(majorId, semester).orElseThrow(() -> new ErrorHandler("Cannot find subject with majorId = " + majorId + " semester " + semester));
    }

    @Override
    public List<Subject> findSubjectBySemesterIdAndMajorId(Integer fromSemester, Integer toSemester, Integer majorId) {
        try {
            return subjectDao.findSubjectsBySemesterIdBetweenAndMajorId(fromSemester, toSemester, majorId);
        } catch (Exception e) {
            throw new ErrorHandler("Don't find any records");
        }
    }
}
