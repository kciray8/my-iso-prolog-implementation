package iaroslav.baranov.tracklog;


import iaroslav.baranov.tracklog.ide.outer.SourceWatcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = "iaroslav.baranov.tracklog",
        exclude = { DataSourceAutoConfiguration.class }
)
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TestApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}
