package br.edu.ifs.academico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AcademicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcademicoApplication.class, args);
	}

}
