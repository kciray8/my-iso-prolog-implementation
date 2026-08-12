package iaroslav.baranov.tracklog.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "iaroslav.baranov.tracklog")
public class WebApplication {

    static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
