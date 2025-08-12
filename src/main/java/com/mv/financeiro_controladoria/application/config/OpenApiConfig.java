package com.mv.financeiro_controladoria.application.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "XPTO Financeiro & Controladoria API",
                version = "v1",
                description = "API para controle de receitas, despesas, clientes, contas e relatórios (XPTO).",
                contact = @Contact(name = "Seu Nome", email = "seu.email@exemplo.com"),
                license = @License(name = "MIT")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Dev")
        }
)
@Configuration
public class OpenApiConfig {

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
                .pathsToMatch("/api/accounts/**", "/api/clients/*/accounts/**")
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
                .pathsToMatch("/api/reports/**", "/api/company/**")
                .build();
    }
}
