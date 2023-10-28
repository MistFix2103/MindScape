package ru.mtuci.MindScape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.mtuci.MindScape.config.YamlPropertySourceFactory;

@SpringBootApplication
@PropertySource(name = "email-templates", value = "classpath:email-templates.yml", factory = YamlPropertySourceFactory.class)
public class MindScapeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MindScapeApplication.class, args);
	}
}
