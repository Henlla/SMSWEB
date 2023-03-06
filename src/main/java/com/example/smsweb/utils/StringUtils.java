package com.example.smsweb.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {
    public static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    public static String randomStudentCard(String numbers){
        String studentCard = "Student"+ RandomStringUtils.random(7,0,numbers.length(),true,true,numbers.toCharArray());
        return studentCard;
    }

    public static String randomTeacherCard(String numbers){
        String teacherCard = "Teacher"+ RandomStringUtils.random(7,0,numbers.length(),true,true,numbers.toCharArray());
        return teacherCard;
    }
}
