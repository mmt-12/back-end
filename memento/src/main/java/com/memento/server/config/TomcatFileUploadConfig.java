package com.memento.server.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatFileUploadConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatFileUploadCustomizer(
            FileUploadProperties fileUploadProperties) {
        return factory -> factory.addContextCustomizers(context -> {
            int maxFileCount = Math.max(1, fileUploadProperties.getPost().getMaxFileCount());
            context.addParameter("org.apache.tomcat.util.http.fileupload.fileCountMax", String.valueOf(maxFileCount));
        });
    }
}
