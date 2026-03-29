package org.spartahub.userservice.infrastructure.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "BearerAuth";

        // 전역 보안 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // JWT SecurityScheme 설정
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("발급받은 Access Token을 입력하세요. (Bearer 키워드 제외)"));

        return new OpenAPI()
                .info(new Info()
                        .title("User Service REST API")
                        .description("스파르타 물류 User Service REST API.")
                        .version("1.0")
                        .contact(new Contact().email("yonggyo00@kakao.com")))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
