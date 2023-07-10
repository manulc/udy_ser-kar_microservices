package com.mlorenzo.photoapp.api.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// En las últimas versiones de Spring Cloud, ya no hace falta añadir esta anotación para el registro de una aplicación, o microservicio,(En este caso un Api Gateway) en un servidor de descubrimiento Eureka
// Es decir, si en el classpath de la aplicación se localiza la dependencia, o librería, "spring-cloud-starter-netflix-eureka-client", automáticamente se procede al registro de la aplicación en el servidor Eureka
//@EnableDiscoveryClient
public class PhotoAppApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoAppApiGatewayApplication.class, args);
	}
}
