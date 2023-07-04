package com.mlorenzo.photoapp.api.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableEurekaClient // Opcional, ya que si en el classpath de la aplicación se localiza la dependencia, o librería, "spring-cloud-starter-netflix-eureka-client", automáticamente se procede al registro de la aplicación en el servidor Eureka
@EnableZuulProxy
@SpringBootApplication
public class PhotoAppZuulApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoAppZuulApiGatewayApplication.class, args);
	}
}
