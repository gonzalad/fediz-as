package org.gonzalad.oidc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"/WEB-INF/applicationContext.xml", "/WEB-INF/security-config.xml"})
public class OidcApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(OidcApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OidcApplication.class);
    }
}
