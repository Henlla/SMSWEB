package com.example.smsweb.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
public class FileUtils {

    public String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + getExtension(originalFileName);
    }

    public String getExtension(String originalFileName) {
        return StringUtils.getFilenameExtension(originalFileName);
    }
}
