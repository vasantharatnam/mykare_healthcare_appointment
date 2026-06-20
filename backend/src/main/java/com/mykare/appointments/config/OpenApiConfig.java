package com.mykare.appointments.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Mykare Appointment Platform API",
                version = "v1",
                description = "APIs for authentication, appointment booking, slot discovery, and appointment processing.",
                contact = @Contact(name = "MyKare Assignment")
                
        ),
        servers = {
               @Server(url = "http://localhost:8080", description = "Local development")
        }
)

public class OpenApiConfig {
    // No additional configuration needed for basic setup
}