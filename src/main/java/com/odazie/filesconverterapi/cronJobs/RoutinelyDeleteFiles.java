package com.odazie.filesconverterapi.cronJobs;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoutinelyDeleteFiles {

//    @Scheduled(fixedDelay = 20000)
//    public void deleteFilesScheduledTask() throws IOException {
//        Files.walk(Paths.get("files"))
//                .filter(Files::isRegularFile)
//                .map(Path::toFile)
//                .forEach(File::delete);
//    }


    @Scheduled(fixedDelay = 60000)
    public void deleteFilesScheduledTask() throws IOException {
        findFiles("files");
    }

    public void findFiles(String filePath) throws IOException {
        List<File> files = Files.list(Paths.get(filePath))
                .map(path -> path.toFile())
                .collect(Collectors.toList());
        for(File file: files) {
            if(file.isDirectory()) {
                findFiles(file.getAbsolutePath());
            } else if(isFileOld(file)){
                deleteFile(file);
            }
        }

    }

    public void deleteFile(File file) {
        file.delete();
    }

    public boolean isFileOld(File file) {
        LocalDate fileDate = Instant.ofEpochMilli(file.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate oldDate = LocalDate.now().minusDays(1);
        return fileDate.isBefore(oldDate);
    }

}
