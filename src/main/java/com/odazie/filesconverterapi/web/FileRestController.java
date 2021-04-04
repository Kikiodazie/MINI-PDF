package com.odazie.filesconverterapi.web;

import com.odazie.filesconverterapi.payload.UploadFileResponse;
import com.odazie.filesconverterapi.service.FileStorageService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@RestController
@Api(description = "PDF CONVERTER ENDPOINTS")
public class FileRestController {
    private static final Logger logger = LoggerFactory.getLogger(FileRestController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/convert-pdf-docx")
    public UploadFileResponse convertPDFtoDocx(@RequestParam("file") MultipartFile file) throws IOException {
        fileStorageService.storeFile(file);
        MultipartFile convertPDFtoDOC = fileStorageService.convertPDFtoDOC(file);
        String fileName = fileStorageService.storeFile(convertPDFtoDOC);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/convert-doc-pdf")
    public UploadFileResponse convertDOCXtoPDF(@RequestParam("file") MultipartFile file) throws Exception {
        fileStorageService.storeFile(file);
        MultipartFile convertDOCtoPDF = fileStorageService.convertDOCtoPDF(file);
        String fileName = fileStorageService.storeFile(convertDOCtoPDF);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/convert-image-pdf")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        fileStorageService.storeFile(file);
        MultipartFile convertImageToPDF = fileStorageService.convertImageToPDF(file);
        String fileName = fileStorageService.storeFile(convertImageToPDF);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }



}
