package com.odazie.filesconverterapi;

import com.odazie.filesconverterapi.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class FilesConverterApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilesConverterApiApplication.class, args);
    }

}
