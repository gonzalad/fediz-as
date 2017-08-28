package org.gonzalad.sts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"/WEB-INF/applicationContext.xml"})
public class StsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(StsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StsApplication.class);
    }
}
