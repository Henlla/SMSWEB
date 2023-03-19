package com.example.smsweb.dto.teacher;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter @EqualsAndHashCode
public class MarkList {
    private String studentCode;
    private String subjectCode;
    private Double asmMark;
    private Double objMark;
}
