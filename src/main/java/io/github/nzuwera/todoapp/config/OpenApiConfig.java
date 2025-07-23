package io.github.nzuwera.todoapp.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
        public static final String DESCRIPTION_OK = "The request completed successfully.";
    public static final String DESCRIPTION_CREATED = "The resource was created successfully.";
    public static final String DESCRIPTION_NO_CONTENT =
            "The request completed successfully and there is no content in the response body.";
    public static final String DESCRIPTION_BAD_REQUEST = "There was an error with the request data.";
    public static final String DESCRIPTION_NOT_FOUND = "The resource with the given id could not be found.";
    public static final String DESCRIPTION_INTERNAL_SERVER_ERROR = "Internal server error.";
    public static final String DESCRIPTION_CONFLICT =
            "The request could not be completed due to a conflict with the current state of the resource.";
    public static final String DESCRIPTION_CONTENT_TOO_LARGE =
            "Requested resource is larger than limits defined by server.";
    @Bean
    public OpenAPI customOpenAPI() {
        var title = getClass().getPackage().getImplementationTitle();
        var version = getClass().getPackage().getImplementationVersion();
        if (title == null) title = "Task API (Dev)";
        if (version == null) version = "dev-snapshot";
        System.out.println("Title=%s, Version=%s".formatted(title,version));
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description("Reactive Task Management API with WebFlux"));
    }
}

