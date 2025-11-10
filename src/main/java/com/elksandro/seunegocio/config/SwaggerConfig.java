package com.elksandro.seunegocio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(info());
    }

    private Info info() {
        return new Info()
            .title("Seu Negócio API - Marketplace Rio Tinto")
            .description("API desenvolvida para o marketplace de pequenos empreendedores.")
            .version("v1.0.0")
            .license(new License()
                    .name("Apache License 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }
}
