package io.github.tawdi.jobboard.company_offers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CompanyOffersApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompanyOffersApplication.class, args);
	}

}
