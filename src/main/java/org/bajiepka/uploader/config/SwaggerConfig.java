package org.bajiepka.uploader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    public class SwaggerConf {

        @Bean
        public Docket api() {
            return new Docket(DocumentationType.SWAGGER_2).select()
                    .apis(RequestHandlerSelectors.basePackage("org.bajiepka.uploader.controller"))
                    .paths(PathSelectors.any()).build()
                    .apiInfo(apiInfo());
        }

        private ApiInfo apiInfo() {
            return new ApiInfo("Сервис хранения файлов",
                    "Сервис предназначен для хранения и предоставления файлов по уникальному идентификатору",
                    "1.0",
                    "Apache 2.0",
                    "Valerii C.",
                    "Custom API license",
                    "http://nosite.com");
        }
    }
}
