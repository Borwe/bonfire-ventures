package com.borwe.bonfireadventures.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.borwe.bonfireadventures.*","com.borwe.bonfireadventures.data.objects"} )
@EntityScan(basePackages = "com.borwe.bonfireadventures.data.objects")
@EnableJpaRepositories(basePackages = {"com.borwe.bonfireadventures.data.objects"})
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
