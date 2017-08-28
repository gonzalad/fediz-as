package org.gonzalad.idp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"/WEB-INF/idp-servlet.xml", "/WEB-INF/applicationContext.xml"})
public class IdpApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(IdpApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(IdpApplication.class);
    }
}
