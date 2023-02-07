package com.example.smsweb.api.service;
import com.example.smsweb.api.di.repository.ProfileRepository;
import com.example.smsweb.api.exception.ErrorHandler;
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
public class ProfileService{
    @Autowired
    private ProfileRepository repository;
    private final String PATH_SAVE = "avatar/";

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
    // save file to Firebase , params is MultipartFile
    // return : Path
    public String saveFile(MultipartFile file) throws IOException {
        String pathFile = PATH_SAVE + fileUtils.generateFileName(file.getOriginalFilename());
        Bucket bucket = StorageClient.getInstance().bucket();
        bucket.create(pathFile, file.getBytes(), file.getContentType());
        return pathFile;
    }

    //format path file to URL firebase
    public String getURLFile(String pathFile) {
        return String.format(environment.getProperty("firebase.image-url"), pathFile);
    }


    public Profile save(Profile profile) {
        //gen image PATH to URL
        String imageUrl = getURLFile(profile.getAvatarPath());
        profile.setAvartarUrl(imageUrl);
        Profile profileAccount = repository.save(profile);
        return profileAccount;
    }


    public void update(Profile profile) {
        repository.save(profile);
    }

    //check path is exists in firebase bucket

    public Profile changeImageProfile(Integer id,MultipartFile file) throws IOException {
        Profile profile = repository.findById(id).orElseThrow(()->new ErrorHandler("Not found with profile id = "+id));
        Bucket bucket = StorageClient.getInstance().bucket();
        boolean isFind = bucket.get(profile.getAvatarPath()).delete();
        if(isFind){
            String pathFile = PATH_SAVE + fileUtils.generateFileName(file.getOriginalFilename());
            profile.setAvatarPath(pathFile);
            bucket.create(pathFile,file.getBytes(),file.getContentType());
            String imageUrl = getURLFile(pathFile);
            profile.setAvartarUrl(imageUrl);
            repository.save(profile);
            return profile;
        }
        return null;
    }

    public void delete(int id) {
        repository.deleteById(id);
    }


    public List<Profile> findAll() {
        return repository.findAll();
    }


    public Profile findOne(int id) {
        return repository.findById(id).orElseThrow(()->new ErrorHandler("Not found with profile id = "+id));
    }
}
