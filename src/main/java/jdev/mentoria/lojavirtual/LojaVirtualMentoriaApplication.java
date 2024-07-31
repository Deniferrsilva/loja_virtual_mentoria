package jdev.mentoria.lojavirtual;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages = "jdev.mentoria.lojavirtual.model") // mapea o banco
@ComponentScan(basePackages = { "jdev.*" }) // scanea todos os pacotes
@EnableJpaRepositories(basePackages = { "jdev.mentoria.lojavirtual.repository" }) // repositorio
@EnableTransactionManagement
public class LojaVirtualMentoriaApplication {

	public static void main(String[] args) {

		// byte[] key = new byte[64];
		// new SecureRandom().nextBytes(key);
		// String encodedKey = Base64.getEncoder().encodeToString(key);
		// System.out.println(encodedKey);

//		System.out.println(new BCryptPasswordEncoder().encode("123"));//
		SpringApplication.run(LojaVirtualMentoriaApplication.class, args);
	}

}
