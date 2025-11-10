package com.fund.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Business 模块 OpenAPI 配置
 */
@Configuration
class OpenApiConfig {

    @Value("\${server.port:9091}")
    private var serverPort: Int = 9091

    @Bean
    fun businessOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Fund 业务接口文档")
                    .description("Fund 项目业务模块 API 接口文档，包括用户、股票、钱包、充值提现等功能")
                    .version("v1.0.0")
                    .contact(
                        Contact()
                            .name("Fund Team")
                            .email("support@fund.com")
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                    )
            )
            .servers(
                listOf(
                    Server().url("http://localhost:$serverPort/api").description("本地开发环境"),
                    Server().url("http://dev.business.example.com/api").description("开发环境"),
                    Server().url("http://prod.business.example.com/api").description("生产环境")
                )
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "Authorization",
                        SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .name("Authorization")
                            .`in`(SecurityScheme.In.HEADER)
                            .description("用户认证 Token，从登录接口获取")
                    )
                    .addSecuritySchemes(
                        "Accept-Language",
                        SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .name("Accept-Language")
                            .`in`(SecurityScheme.In.HEADER)
                            .description("语言设置，支持 en-US, zh-CN")
                    )
            )
            .addSecurityItem(
                SecurityRequirement()
                    .addList("Authorization")
            )
    }
}

