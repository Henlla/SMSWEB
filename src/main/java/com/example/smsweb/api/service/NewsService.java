package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.INews;
import com.example.smsweb.api.di.repository.NewsRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.News;
import com.example.smsweb.models.Profile;
import com.example.smsweb.utils.FileUtils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class NewsService {
    @Autowired
    private NewsRepository dao;
    private final String PATH_SAVE = "news/";

    @Autowired
    Environment environment;

    @Autowired
    private FileUtils fileUtils;

    private FirebaseOptions options;

    @EventListener
    public void init(ApplicationReadyEvent event) {
        try {
            ClassPathResource serviceAccount = new ClassPathResource("sms-project-a5152-firebase-adminsdk-k0vzt-f793c0f483.json");
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                    .setStorageBucket(environment.getProperty("firebase.bucket-name"))
                    .build();
            FirebaseApp.initializeApp(options);

        } catch (Exception ex) {

            ex.printStackTrace();

        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        String pathFile = PATH_SAVE + fileUtils.generateFileName(file.getOriginalFilename());
        Bucket bucket = StorageClient.getInstance().bucket();
        bucket.create(pathFile, file.getBytes(), file.getContentType());
        return pathFile;
    }

    public String getURLFile(String pathFile) {
        return String.format(environment.getProperty("firebase.image-url"), pathFile);
    }

    public void update(News news) {
        dao.save(news);
    }

    public News save(News news) {
        //gen image PATH to URL
        String imageUrl = getURLFile(news.getThumbnailPath());
        news.setThumbnailUrl(imageUrl);
        News newSave = dao.save(news);
        return newSave;
    }

    public News changeImageProfile(Integer id,MultipartFile file) throws IOException {
        News news = dao.findById(id).orElseThrow(()->new ErrorHandler("Not found with profile id = "+id));
        Bucket bucket = StorageClient.getInstance().bucket();
        boolean isFind = bucket.get(news.getThumbnailPath()).delete();
        if(isFind){
            String pathFile = PATH_SAVE + fileUtils.generateFileName(file.getOriginalFilename());
            news.setThumbnailPath(pathFile);
            bucket.create(pathFile,file.getBytes(),file.getContentType());
            String imageUrl = getURLFile(pathFile);
            news.setThumbnailUrl(imageUrl);
            dao.save(news);
            return news;
        }
        return null;
    }

    public void delete(int id) {
        try {
            dao.deleteById(id);
        } catch (Exception e) {
            throw new ErrorHandler("Xóa thất bại");
        }
    }

    public List<News> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new ErrorHandler("Không tìm thấy dữ liệu");
        }
    }

    public News findOne(int id) {
        try {
            return dao.findById(id).get();
        }catch (Exception e){
            throw new ErrorHandler("Không tìm thấy dữ liệu với id " + id);
        }
    }
}
