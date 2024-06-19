package MyImdb.demo.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocumentationConfig {

    @Bean
    public OpenAPI apiDocConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("Personal IMDB API")
                        .description("Personal IMDB API description")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Diogo Correia")
                                .email("diogo.h.correia@gmail.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentation"));
    }

}