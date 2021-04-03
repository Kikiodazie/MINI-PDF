package com.odazie.filesconverterapi.service;

import com.aspose.pdf.Document;
import com.aspose.pdf.SaveFormat;
import com.odazie.filesconverterapi.exception.FileStorageException;
import com.odazie.filesconverterapi.exception.MyFileNotFoundException;
import com.odazie.filesconverterapi.property.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {


    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties){
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        }catch (Exception exception){
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", exception);
        }
    }


    public MultipartFile convertPDFtoDOC(MultipartFile file){

        Document document = new Document("files/"+ file.getOriginalFilename());
        String savingFileName = file.getOriginalFilename().replace(".pdf", "")+".docx";
        document.save( savingFileName, SaveFormat.DocX);

        Path  path = Paths.get(savingFileName);
        String contentType = "text/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        MultipartFile result = new MockMultipartFile(savingFileName,
                savingFileName, contentType, content);

        return result;
    }

    public MultipartFile convertDOCtoPDF(MultipartFile file) throws Exception {

        com.aspose.words.Document document = new com.aspose.words.Document("files/"+ file.getOriginalFilename());
        String savingFileName = file.getOriginalFilename().replace(".docx", "")+".pdf";

        document.save(savingFileName);

        Path  path = Paths.get(savingFileName);

        String contentType = "application/pdf";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        MultipartFile result = new MockMultipartFile(savingFileName,
                savingFileName, contentType, content);

        return result;
    }

    public MultipartFile convertJPEGtoPDF(MultipartFile file){
        return null;
    }


    public String storeFile(MultipartFile file) throws IOException {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            Files.deleteIfExists(Paths.get(file.getOriginalFilename()));
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }



    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }




}
