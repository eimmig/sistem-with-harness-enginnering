package com.projetosenior.gestaohospedes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gestaoHospedesOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gestão de Hóspedes API")
                        .description(
                                "API de gestão de hóspedes para um hotel: cadastro de hóspedes, "
                                        + "reservas, check-in e check-out, com cálculo automático de "
                                        + "diárias, taxa de estacionamento e taxa de atraso na saída.")
                        .version("v0"));
    }
}
