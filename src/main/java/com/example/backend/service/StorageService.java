package com.example.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService {

    public static void saveImage(MultipartFile imageFile, String date) throws Exception {

        String folder = "/media/photos/";
        File uploadDir = new File(folder)
                ;
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        byte[] bytes = imageFile.getBytes();
        Path path = Paths.get(folder + date + "_" + imageFile.getOriginalFilename());
        Files.write(path, bytes);
    }

    public static void updateImage(MultipartFile imageFile, String oldimg, String date) throws Exception {

        String folder = "/media/photos/";
        File fileToDelete = new File("/media/photos/" + oldimg);
        File uploadDir = new File(folder)
                ;
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        if (oldimg != null) FileSystemUtils.deleteRecursively(fileToDelete);
        byte[] bytes = imageFile.getBytes();
        Path path = Paths.get(folder + date + "_" + imageFile.getOriginalFilename());
        Files.write(path, bytes);
    }

}
