package com.example.smsweb.dto.teacher;

import com.example.smsweb.models.Student;
import com.example.smsweb.models.Subject;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter @EqualsAndHashCode
public class InputMarkModel implements Serializable {
    private int studentId;
    private String studentCode;
    private int profileId;
    private String firstName;
    private String lastName;
    private String fullName;


    private int subjectId;
    private String subjectCode;
    private String subjectName;
    private int studentSubjectId;
    private Double asmMark;
    private Double objMark;

    public InputMarkModel(Student student, Subject subject) {
        this.studentSubjectId = student.getStudentSubjectsById().stream()
                .filter(p -> p.getSubjectId() == subject.getId())
                .findFirst().get().getId();
        this.studentId = student.getId();
        this.studentCode = student.getStudentCard();
        this.profileId = student.getProfileId();
        this.firstName = student.getStudentByProfile().getFirstName();
        this.lastName = student.getStudentByProfile().getLastName();
        this.fullName = student.getStudentByProfile().getFullName();
        this.subjectId = subject.getId();
        this.subjectCode = subject.getSubjectCode();
        this.subjectName = subject.getSubjectName();
    }

    public InputMarkModel(String studentCode, String subjectCode, Double asmMark, Double objMark) {
        this.studentCode = studentCode;
        this.subjectCode = subjectCode;
        this.asmMark = asmMark;
        this.objMark = objMark;
    }
}
