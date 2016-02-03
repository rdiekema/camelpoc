package org.diekema.camelpoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:processor-beans.xml")
@ComponentScan(basePackages = {"org.diekema.camelpoc.processors", "org.diekema.camelpoc.routes"})
public class CamelpocApplication {

	public static void main(String[] args) {
		SpringApplication.run(CamelpocApplication.class, args);
	}
}
