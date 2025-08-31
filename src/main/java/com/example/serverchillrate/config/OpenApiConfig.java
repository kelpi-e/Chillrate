package com.example.serverchillrate.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
@OpenAPIDefinition(
        info = @Info(
                title = "Chillrate API",
                description = "Chilrate Api", version = "1.0.0",
                contact = @Contact(
                        name = "Dmitriy Komarov",
                        url = "https://github.com/Myrmlok/ChillrateServer"
                )
        )
)
public class OpenApiConfig {
}
