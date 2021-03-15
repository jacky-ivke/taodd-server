package com.esports.core.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Data
public class SwaggerConfig {

	@Value("${swagger.enabled}")
	private Boolean swaggerEnabled;


	@Bean
	public Docket createRestApi() {
		boolean externallyConfiguredFlag =(null == swaggerEnabled)? Boolean.TRUE : swaggerEnabled;
		return new Docket(DocumentationType.SWAGGER_2)
				.enable(externallyConfiguredFlag).groupName("spring-boot")
				.apiInfo(apiInfo())
				.select().apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
				.paths(PathSelectors.any()).build();
	}

	protected ApiInfo apiInfo() {
		
	    return new ApiInfoBuilder()
				// 页面标题
				.title("应用接口服务系统")
				// 创建人
				.contact(new Contact("海贼王", "http://www.showdoc.cc", ""))
				// 版本号
				.version("1.0.0").license("API接口,需授权认证,接口参数值如果包含关键字,将拒绝服务")
				// 描述
				.description("API设计开发完成后，在保证API完整性和稳定性的情况下，需要对API接口进行详细的说明，接口设计必须按照标准实施").build();
	}
}
