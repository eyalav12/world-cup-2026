package com.world_cup.demo.service.startupservices;

import com.world_cup.demo.service.CsvLoaderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartUpFileLoaderCommandRunner implements CommandLineRunner {
    private CsvLoaderService csvLoaderService;

    public StartUpFileLoaderCommandRunner(CsvLoaderService csvLoaderService){
        this.csvLoaderService = csvLoaderService;
    }
    @Override
    public void run(String... args) throws Exception {
        csvLoaderService.handleFileLoader();
    }
}
