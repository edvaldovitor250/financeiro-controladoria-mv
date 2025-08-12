package com.mv.financeiro_controladoria.application.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "XPTO Financeiro & Controladoria API",
                version = "v1",
                description = "API para clientes, contas, movimentações e relatórios.",
                contact = @Contact(name = "Seu Nome", email = "seu.email@exemplo.com"),
                license = @License(name = "MIT")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Dev")
        },
        externalDocs = @ExternalDocumentation(
                description = "Repositório GitHub",
                url = "https://github.com/seu-user/seu-repo"
        )
)
@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi allGroup() {
        return GroupedOpenApi.builder()
                .group("all")
                .packagesToScan("com.mv.financeiro_controladoria.api")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi clientsGroup() {
        return GroupedOpenApi.builder()
                .group("clients")
                .packagesToScan("com.mv.financeiro_controladoria.api")
                .pathsToMatch("/api/clients/**")
                .build();
    }

    @Bean
    public GroupedOpenApi accountsGroup() {
        return GroupedOpenApi.builder()
                .group("accounts")
                .packagesToScan("com.mv.financeiro_controladoria.api")
                .pathsToMatch("/api/accounts/**", "/api/clients/**/accounts/**")
                .build();
    }

    @Bean
    public GroupedOpenApi movementsGroup() {
        return GroupedOpenApi.builder()
                .group("movements")
                .packagesToScan("com.mv.financeiro_controladoria.api")
                .pathsToMatch("/api/movements/**")
                .build();
    }

    @Bean
    public GroupedOpenApi reportsGroup() {
        return GroupedOpenApi.builder()
                .group("reports")
                .packagesToScan("com.mv.financeiro_controladoria.api")
                .pathsToMatch("/api/reports/**")
                .build();
    }

    @Bean
    public GroupedOpenApi companyGroup() {
        return GroupedOpenApi.builder()
                .group("company")
                .packagesToScan("com.mv.financeiro_controladoria.api")
                .pathsToMatch("/api/company/**")
                .build();
    }
}
