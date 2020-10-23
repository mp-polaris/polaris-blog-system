package com.polaris.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Value("${polaris.blog.swagger.enable}")
    private boolean isEnable;

    public static final String VERSION = "1.0.0";

    /**
     * 门户Api，接口前缀为portal
     * @return
     */
    @Bean
    public Docket portalApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(portalApiInfo())
                .enable(isEnable)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.polaris.blog.controller.portal"))
                .paths(PathSelectors.any())
                .build()
                .groupName("前端门户");
    }

    /**
     * 设置portalApi的ApiInfo
     * @return
     */
    private ApiInfo portalApiInfo() {
        return new ApiInfoBuilder()
                .title("polaris博客系统门户接口文档")
                .description("门户接口文档")
                .version(VERSION)
                .build();
    }

    /**
     * 管理中心api，接口前缀为admin
     * @return
     */
    @Bean
    public Docket adminApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(adminApiInfo())
                .enable(isEnable)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.polaris.blog.controller.admin"))
                .paths(PathSelectors.any())
                .build()
                .groupName("管理中心");
    }


    private ApiInfo adminApiInfo() {
        return new ApiInfoBuilder()
                .title("polaris博客系统门户接口文档")
                .description("管理中心接口")
                .version(VERSION)
                .build();
    }


    @Bean
    public Docket UserApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(userApiInfo())
                .enable(isEnable)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.polaris.blog.controller.user"))
                .paths(PathSelectors.any())
                .build()
                .groupName("用户中心");
    }

    private ApiInfo userApiInfo() {
        return new ApiInfoBuilder()
                .title("polaris博客系统用户接口")
                .description("用户接口的接口")
                .version(VERSION)
                .build();
    }

}
