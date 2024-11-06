package org.example.funkosProject.storage.config;

import org.example.funkosProject.storage.services.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("storage")
public class StorageConfig {

    @Bean
    public CommandLineRunner init(StorageService storageService, @Value("${upload.delete}") String deleteAll) {
        return args -> {
            if (deleteAll.equals("true")) {
                storageService.deleteAll();
            }

            storageService.init();
        };
    }
}
