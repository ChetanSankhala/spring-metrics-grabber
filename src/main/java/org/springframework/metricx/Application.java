package org.springframework.metricx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] appArguments) {
        final SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.run(appArguments);
    }
}