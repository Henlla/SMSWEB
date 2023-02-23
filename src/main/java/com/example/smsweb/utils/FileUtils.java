package com.example.smsweb.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.UUID;

@Component
public class FileUtils {

    public String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + getExtension(originalFileName);
    }

    public static String getExtension(String originalFileName) {
        return StringUtils.getFilenameExtension(originalFileName);
    }

    public static void uploadFile(String fileName, String pathStore, MultipartFile file) {
        InputStream is;
        OutputStream ot;
        String rootPath = System.getProperty("user.dir");
        File uploadDir = new File(rootPath + pathStore);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        try {
            ot = new FileOutputStream(uploadDir + File.separator + fileName);
            is = file.getInputStream();
            byte[] data = new byte[is.available()];
            is.read(data);
            ot.write(data);
            ot.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileName(MultipartFile file) {
        switch (getExtension(file.getOriginalFilename())) {
            case "docx":
                return file.getOriginalFilename().replace(".docx", "");
            case "png":
                return file.getOriginalFilename().replace(".png", "");
            case "jpeg":
                return file.getOriginalFilename().replace(".jpeg", "");
            case "xlsx":
                return file.getOriginalFilename().replace(".xlsx", "");
            case "jpg":
                return file.getOriginalFilename().replace(".jpg", "");
            default:
                return "wrong format";
        }
    }

    public static File listFilesForFolder(String pathStore) {
        File folder = new File(pathStore);
        File[] listOfFiles = folder.listFiles();
        File fileGet = null;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                fileGet = file;
                System.out.println(file.getName());
            }
        }
        return fileGet;
    }
}
